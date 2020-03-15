import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import util.Config;
import util.Log;
import util.Tools;

public class ActivityManager{

	public static void main(String args[]){

		ActivityManager AM = new ActivityManager();
		while(true)
			AM.handling();
	}


	ClientThreadUTF8 CTU8;

	public ArrayList<String> commandBox = new ArrayList<String>();

	private final Lock commandLock = new ReentrantLock(true);

	boolean running = true;
	String splitWord = ":";
	File activities = new File("activities");
	int reconnect_count = 0;
	Tools T = new Tools();

	MemoryManager MM = new MemoryManager();

	public void handling() {

		Config config = new Config("setting.txt");
		CTU8 = new ClientThreadUTF8(config.Get("ipAdress_server","localhost"),config.Get("portNum_server",11000));
		CTU8.start();


		if(activities.mkdir()){
			new Log("Make NEW Directly 'activities'");

			File temp = new File(activities.getName() + "/"+ "test.txt");
			try {
				temp.createNewFile();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

		if(CTU8.connect) {
			reconnect_count=0;
			CTU8.sendMsg("name;am");

			while(running) {

				String activityCommand = CTU8.getMsg();

				if(activityCommand!=null) {

					File targetActivity = selectActivity(activityCommand);

					if(targetActivity != null) {

						//ArrayList<String> behaviorCommands = new ArrayList<String>();
						//ReadFile(targetActivity, behaviorCommands);


						ArrayList<String> behaviorCommands = ReadBehaviors(targetActivity);

						new Log("Read activity: " + targetActivity);


						// perform behavior commands in activity file
						for(int i=0; i<behaviorCommands.size(); i++) {


							// for waitForCommandEnd
							if(behaviorCommands.get(i).contains("waitForCommandEnd")) {

								String whom = behaviorCommands.get(i).split(";")[0];

								String key = timeStamp();
								CTU8.sendMsg(whom + ";addAction:requestMessage:am;" + key);

								String input = CTU8.getMsg();
								while(input==null) {
									input = CTU8.getMsg();
									T.wait(50);
								}

								//new Log("deburg:" + input + ", " + key);

								if(!input.equals(key)) {
									CTU8.returnMsg(input);
									break;
								}

							}else
								CTU8.sendMsg(behaviorCommands.get(i));


						}

					}

				}


			}
		}else {

			reconnect_count++;
			new Log("wait for re-connecting: " + ((reconnect_count*reconnect_count/2.0)+1) + " second");
			T.wait(((reconnect_count*reconnect_count/2)+1)*1000);
		}

	}





	public File selectActivity(String activityCommand) {

		File activityList[] = activities.listFiles();
		ArrayList<File> targetActivities = new ArrayList<File>();
		File targetActivity = null;


		// looking for activity files
		for(int i=0; i<activityList.length; i++) {

			if(activityList[i].getName().startsWith(activityCommand)) {

				if(activityList[i].getName().equals(activityCommand + ".txt")) {

					targetActivities.add(activityList[i]);

				}else {

					String remain = activityList[i].getName().replaceFirst(activityCommand, "");
					remain = remain.replaceAll(".txt", "");
					try {
						int num = Integer.parseInt(remain);
						targetActivities.add(activityList[i]);
					}catch(NumberFormatException e){
						//new Log("incorrect Number: " + activityList[i].getName());
					}
				}
			}
		}

		if(targetActivities.size()==0) {

			new Log("Activity ERROR: Could not find activity: " + activityCommand);

		}else if(targetActivities.size()==1) {

			targetActivity = targetActivities.get(0);

		}else{	// removed performed activities by referring memory


			String history = MM.Read("long_memory.txt", activityCommand + "_history");

			ArrayList<File> targetActivities_temp = new ArrayList<File>();
			for(int i=0; i<targetActivities.size(); i++) {
				targetActivities_temp.add(targetActivities.get(i));
				//new Log("debug " + targetActivities.get(i).getName());
			}


			if(history!=null) {
				String histories[] = history.split(MM.splitWord2);

				for(int i=0; i<histories.length; i++) {

					for(int j=targetActivities.size()-1; j>=0; j--) {

						if(targetActivities.get(j).getName().equals(histories[i] + ".txt")) {

								//new Log("debug " + targetActivities.get(j).getName());
								//new Log("debug " + histories[i] + ".txt");
								targetActivities.remove(j);
						}
					}
				}
			}

			if(targetActivities.size()==0) {	// reset when already performed all

				for(int i=0; i<targetActivities_temp.size(); i++)
					targetActivities.add(targetActivities_temp.get(i));

				MM.Clear("long_memory.txt", activityCommand + "_history");
			}



			long seed = System.currentTimeMillis();
			Random rand = new Random(seed);
			int flag = rand.nextInt(targetActivities.size());
			targetActivity = targetActivities.get(flag);

			MM.Add("long_memory.txt", activityCommand + "_history", targetActivity.getName().replace(".txt", ""));
			MM.Add("short_memory.txt", "activity_history", targetActivity.getName().replace(".txt", ""));


		}


		return targetActivity;
	}














	public String getCommand() {

		commandLock.lock();

		String temp = manageCommandQueue("DEQ", null);

		commandLock.unlock();

		return temp;
	}

	public void addCommand(String command) {

		commandLock.lock();

		manageCommandQueue("ENQ", command);

		commandLock.unlock();
	}


	public String manageCommandQueue(String command, String addMsg) {

		switch(command) {

			case "DEQ":
				if(commandBox.size()!=0) {

					String getMsg = commandBox.get(0);
					if(getMsg != null) {
						commandBox.remove(0);
						return getMsg;
					}else
						return null;

				}else
					return null;

			case "ENQ":
				commandBox.add(addMsg);

				if(commandBox.size()>10) {

					new Log("manageCommandQueue too much stack(>10): remove command: " + commandBox.get(0));
					commandBox.remove(0);

				}

				return null;

			default:
				//CRobotUtil.Log(TAG, "manageMsgQueue command error: " + command);
				return null;

		}


	}

	public ArrayList<String> ReadBehaviors(File file) {

		ArrayList<String> behaviorCommands = new ArrayList<String>();
		ArrayList<String> rBehaviorCommands = new ArrayList<String>();

		ReadFile(file, behaviorCommands);



		for(int i=0; i<behaviorCommands.size(); i++) {
			//System.out.println("deburg0: " + behaviorCommands.get(i));

			if(behaviorCommands.get(i).startsWith("loadActivity" + splitWord)) {

				// command = loadActivity:**
				String activityCommand = behaviorCommands.get(i).replaceFirst("loadActivity" + splitWord, "");

				File targetActivity = selectActivity(activityCommand);

				ArrayList<String> aditionalBehaviorCommands = ReadBehaviors(targetActivity);

				new Log("Load Activity: " + activityCommand);


				for(int j=0; j<aditionalBehaviorCommands.size(); j++)
					rBehaviorCommands.add(aditionalBehaviorCommands.get(j));

			//}else if(behaviorCommands.get(i).contains("ifContains") || behaviorCommands.get(i).contains("ifNotContains")) {
			}else if(behaviorCommands.get(i).startsWith("if" + splitWord)) {
				// command = if:[**.txt:ifContains()=**,**&**.txt:notIfContains()=**]|**.txt:ifContains()=**,**;behaviorCommands

				//String bc = behaviorCommands.get(i).replace(behaviorCommands.get(i).split(splitWord)[0]+splitWord, "");
				String commands[] = behaviorCommands.get(i).replaceFirst("if" + splitWord, "").split(";");
				if(judgeCondition(commands[0])) {
					behaviorCommands.add(i+1, behaviorCommands.get(i).replace(commands[0]+";", ""));
				}else {
					//new Log("Pass the code: " + behaviorCommands.get(i));
				}
				//behaviorCommands.add(i+1, commands[1]);

			}else if(behaviorCommands.get(i).startsWith("addMemory" + splitWord)) {

				// command = addMemory:**.txt:(key)=(memory)
				String addMemories[] = behaviorCommands.get(i).split(splitWord);
				if(addMemories.length==3) {
					if(addMemories[1].endsWith(".txt")) {
						String keys[] = addMemories[2].split("=");
						if(keys.length==2) {
							MM.Add(addMemories[1], keys[0], keys[1]);
							new Log("addMemory: " + behaviorCommands.get(i));
						}else
							new Log("addMemory Error(keyword): " + behaviorCommands.get(i));
					}else
						new Log("addMemory Error(text file): " + behaviorCommands.get(i));
				}else
					new Log("addMemory Error(code!=3): " + behaviorCommands.get(i));


			}else if(behaviorCommands.get(i).startsWith("clearMemory" + splitWord)) {

				// command = clearMemory:**.txt:(key)
				String addMemories[] = behaviorCommands.get(i).split(splitWord);
				if(addMemories.length==3) {
					if(addMemories[1].endsWith(".txt")) {
						MM.Clear(addMemories[1], addMemories[2]);
					}else
						new Log("clearMemory Error(text file): " + behaviorCommands.get(i));
				}else if(addMemories.length==2) {
					if(addMemories[1].endsWith(".txt")) {
						MM.Clear(addMemories[1]);
					}else
						new Log("clearMemory Error(text file): " + behaviorCommands.get(i));
				}else
					new Log("clearMemory Error(code): " + behaviorCommands.get(i));


			}else if(behaviorCommands.get(i).startsWith("overwriteMemory" + splitWord)) {

				// command = addMemory:**.txt:(key)=(memory)
				String addMemories[] = behaviorCommands.get(i).split(splitWord);
				if(addMemories.length==3) {
					if(addMemories[1].endsWith(".txt")) {
						String keys[] = addMemories[2].split("=");
						if(keys.length==2) {
							MM.Clear(addMemories[1]);
							MM.Add(addMemories[1], keys[0], keys[1]);
							new Log("overwriteMemory: " + behaviorCommands.get(i));
						}else
							new Log("overwriteMemory Error(keyword): " + behaviorCommands.get(i));
					}else
						new Log("overwriteMemory Error(text file): " + behaviorCommands.get(i));
				}else
					new Log("overwriteMemory Error(code!=3): " + behaviorCommands.get(i));


			}else if(behaviorCommands.get(i).contains("waitForCommandEnd")) {

				// command = sota;waitForCommandEnd
				rBehaviorCommands.add(behaviorCommands.get(i));



			}else if(behaviorCommands.get(i).startsWith("scenarioUpdate")) {

				ScenarioUpdater SU = new ScenarioUpdater();
				SU.update();

			}else
				rBehaviorCommands.add(behaviorCommands.get(i));


		}

		return rBehaviorCommands;
	}




	public boolean judgeCondition(String condition) {

		// condition = [ifContains()=**,**:**.txt&notIfContains()=**:**.txt]|ifContains()=**,**:**.txt

		if(condition.equals("true"))
			return true;
		if(condition.equals("false"))
			return false;

		while(true) {

			if(condition.contains("[")&&condition.contains("]")) {
				String subCondition = condition.substring(condition.indexOf("[")+1, condition.indexOf("]"));
				boolean sub_ture_or_false = judgeCondition(subCondition);
				condition = condition.replace(condition.substring(condition.indexOf("["), condition.indexOf("]")+1), String.valueOf(sub_ture_or_false));
				//System.out.println("deburg: " + condition );
			}else
				break;
		}
		//System.out.println("deburg0: " + condition );

		if(condition.contains("&")) {
			String conditions1[] = condition.split("&");
			for(int i=0; i<conditions1.length; i++) {
				if(conditions1[i].contains("|")) {
					String conditions2[] = conditions1[i].split("\\|");
					for(int j=0; j<conditions2.length; j++) {
						condition = condition.replaceFirst(conditions2[j].replaceAll("\\(", "\\"+"\\(").replaceAll("\\)", "\\"+"\\)"), String.valueOf(ifContains(conditions2[j])));
						//System.out.println("deburg&: " + condition );
					}
				}else {
					condition = condition.replaceFirst(conditions1[i].replaceAll("\\(", "\\"+"\\(").replaceAll("\\)", "\\"+"\\)"), String.valueOf(ifContains(conditions1[i])));
					//System.out.println("deburg&2: " + condition );
				}
			}
		}
		if(condition.contains("|")) {
			String conditions1[] = condition.split("\\|");
			for(int i=0; i<conditions1.length; i++) {
				if(conditions1[i].contains("&")) {
					String conditions2[] = conditions1[i].split("&");
					for(int j=0; j<conditions2.length; j++) {
						condition = condition.replaceFirst(conditions2[j].replaceAll("\\(", "\\"+"\\(").replaceAll("\\)", "\\"+"\\)"), String.valueOf(ifContains(conditions2[j])));
						//System.out.println("deburg|: " + condition );
					}
				}else {
					//System.out.println("deburg|2-: " + conditions1[i] );
					condition = condition.replaceFirst(conditions1[i].replaceAll("\\(", "\\"+"\\(").replaceAll("\\)", "\\"+"\\)"), String.valueOf(ifContains(conditions1[i])));
					//System.out.println("deburg|2: " + condition );
				}
			}
		}

		if(!condition.contains("&") && !condition.contains("|"))
			condition = String.valueOf(ifContains(condition));

		//System.out.println("deburg2: " + condition );

		String pre_condition = condition;
		while(condition.contains("&") || condition.contains("|")) {

			if(condition.startsWith("true&true"))
				condition = condition.replaceFirst("true&true", "true");
			if(condition.startsWith("true&false"))
				condition = condition.replaceFirst("true&false", "false");
			if(condition.startsWith("false&true"))
				condition = condition.replaceFirst("false&true", "false");
			if(condition.startsWith("false&false"))
				condition = condition.replaceFirst("false&false", "false");
			if(condition.startsWith("true|true"))
				condition = condition.replaceFirst("true\\|true", "true");
			if(condition.startsWith("true|false"))
				condition = condition.replaceFirst("true\\|false", "false");
			if(condition.startsWith("false|true"))
				condition = condition.replaceFirst("false\\|true", "true");
			if(condition.startsWith("false|false"))
				condition = condition.replaceFirst("false\\|false", "false");

			if(condition.equals(pre_condition)) {
				new Log("judge condition error: " + condition);
				break;
			}
			pre_condition = condition;
		}

		//System.out.println("deburg3: " + condition );

		if(condition.equals("true"))
			return true;
		if(condition.equals("false"))
			return false;

		return false;
	}



	public boolean ifContains(String condition) {

		if(condition.equals("true"))
			return true;
		if(condition.equals("false"))
			return false;


		// command = **.txt:ifContains()=**,**
		String mCommands[] = condition.split(splitWord);
		//String ifs[] = mCommands[0].split("&");
		boolean judge = true;

		if(mCommands[0].endsWith(".txt") && mCommands.length==2) {

			if(mCommands[1].startsWith("ifContains(") && mCommands[1].contains(")=")) {
				String keys[] = mCommands[1].replaceFirst("ifContains\\(", "").split("\\)=");
				if(MM.Read(mCommands[0], keys[0])!=null) {
					for(int i=0; i<keys[1].split(",").length; i++) {
						if(!MM.Read(mCommands[0], keys[0]).contains(keys[1].split(",")[i])) {
							judge = false;
						}
					}
					return judge;
				}else
					new Log("ifContains Error(not find file or keyword): " + condition);
			}else if(mCommands[1].startsWith("ifNotContains(") && mCommands[1].contains(")=")) {
				String keys[] = mCommands[1].replaceFirst("ifNotContains\\(", "").split("\\)=");
				if(MM.Read(mCommands[0], keys[0])!=null) {
					for(int i=0; i<keys[1].split(",").length; i++) {
						if(MM.Read(mCommands[0], keys[0]).contains(keys[1].split(",")[i])) {
							judge = false;
						}
					}
					return judge;
				}else
					new Log("ifNotContains Error(not find file or keyword): " + condition);
			}else
				new Log("ifContains Error('ifContains()='): " + condition);
		}else
			new Log("ifContains Error(.txt): " + condition);
		return false;
	}


	public boolean ReadFile(File file, ArrayList<String> array){ // From text content Put in array ///////////////////////////

		//File file = new File(FileName);

		try{
			String str;
			BufferedReader br;

			// Confirm avaival And open buffer ////////////////////////
			if (IsAvailable(file))
				br = new BufferedReader(new FileReader(file));
			else
				return false;

			// Read each line in the file /////////////////////////
			//new Log("read " + file.getName(),false,4);
			{
				while ((str = br.readLine()) != null) {
					//new Log(str);
					if(!str.startsWith("#") && !str.equals(""))
						array.add(str);
				}
				br.close();
				return true;
			}
		}catch (IOException err) {
			System.out.println(err);
		}
		return false;
	}


	public boolean IsAvailable(File file){ // Let you know if the file is avaival /////////////////////

		if(file==null) {
			new Log ("File EROOR: NULL");
			return false;
		}

		if (file.exists()) {
			if (file.isFile() && file.canRead() && file.canWrite()) {
				return true;
			}
		}
		new Log(file.getPath() + " is Not Available");
		return false;
	}


    String timeStamp() {

    	//String splitWord = "\t";

		Calendar now = Calendar.getInstance();

		 String y = String.valueOf(now.get(now.YEAR));
		 String mo = String.valueOf(now.get(now.MONTH)+1);
		 String d = String.valueOf(now.get(now.DAY_OF_MONTH));
		 String h = String.valueOf(now.get(now.HOUR_OF_DAY));
		 String m = String.valueOf(now.get(now.MINUTE));
		 String s = String.valueOf(now.get(now.SECOND));
		 String ms = String.valueOf(now.get(now.MILLISECOND));


	     return  h + m + s + ms;



    }


}
