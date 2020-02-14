import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import util.Config;
import util.Log;
import util.Tools;

public class StateHandler {

	public static void main(String args[]){

		StateHandler SH = new StateHandler();
		while(true)
			SH.handling();
	}

	String splitWord = ":";
	int reconnect_count = 0;
	Tools T = new Tools();

	String currentState = "launching";
	ArrayList<String> transitionArray = new ArrayList<String>();
	MemoryManager MM = new MemoryManager();


	public StateHandler() {

		ScenarioUpdater SU = new ScenarioUpdater();
		SU.start();

	}

	public void handling() {


		Config config = new Config("setting.txt");
		ClientThreadUTF8 CTU8 = new ClientThreadUTF8(config.Get("ipAdress_server","localhost"),config.Get("portNum_server",11000));
		CTU8.start();

		//ActivityManager AM = new ActivityManager(CTU8);
		//AM.start();


		if(CTU8.connect) {
			reconnect_count=0;

			String initMsg = config.Get("init_message","name;sh");
			CTU8.sendMsg(initMsg);

			String scenarioFile = config.Get("scenario_file","test.txt");
			//FileManage FM = new FileManage();
			reconnect_count = 0;

			config.Set("ipAdress_server", CTU8.ipAdress);
			config.Set("portNum_server", CTU8.portNum);
			config.Set("init_message", initMsg);
			config.Set("scenario_file", scenarioFile);


			currentState = "start";
			readScenario(config.Get("scenario_file","test.txt"), currentState);
			new Log("Load " + config.Get("scenario_file","test.txt"));


			while(CTU8.connect) {



				String triggers = CTU8.getMsg();


				if(triggers!=null) {

					String trigger = triggers.split(splitWord)[0];

					new Log("received: " + trigger + " (current state: " + currentState + " )");


					if(triggers.startsWith("addMemory" + splitWord)) {
						// command = addMemory:**.txt:(key)=(memory)
						String addMemories[] = triggers.split(":");
						if(addMemories.length==3) {
							if(addMemories[1].endsWith(".txt")) {
								String keys[] = addMemories[2].split("=");
								if(keys.length==2) {
									MM.Add(addMemories[1], keys[0], keys[1]);
									new Log("AddMemory: " + triggers);
								}else
									new Log("AddMemory Error(keyword): " + triggers);
							}else
								new Log("AddMemory Error(text file): " + triggers);
						}else
							new Log("AddMemory Error(code!=3): " + triggers);
						continue;
					}else if(triggers.startsWith("clearMemory" + splitWord)) {
						// command = clearMemory:**.txt:(key)
						String addMemories[] = triggers.split(":");
						if(addMemories.length==3) {
							if(addMemories[1].endsWith(".txt")) {
								MM.Clear(addMemories[1], addMemories[2]);
							}else
								new Log("ClearMemory Error(text file): " + triggers);
						}else if(addMemories.length==2) {
							if(addMemories[1].endsWith(".txt")) {
								MM.Clear(addMemories[1]);
							}else
								new Log("AddMemory Error(text file): " + triggers);
						}else
							new Log("AddMemory Error(code): " + triggers);
						continue;
					}


					// find next state
					readScenario(config.Get("scenario_file","test.txt"), currentState);
					ArrayList<String> nextStateArray = collectCodes(trigger);

					String performTransition=null;

					if(nextStateArray.size()>1) { // in the case of multiple choices

						ArrayList<String> selectStateArray = new ArrayList<String>();

						for(int i=0; i<nextStateArray.size(); i++) {

							String str = nextStateArray.get(i);
							String option = str.split(splitWord)[4];

							for(int j=5; j<str.split(splitWord).length; j++)
								option += splitWord + str.split(splitWord)[j];

							// select;**.txt:ifContains()=** or remove;**.txt:ifNotContains()=**
							String mCommands[] = option.split(";");

							// SELECT
							if(option.startsWith("select;")) {
								if(judgeCondition(mCommands[1]))
									selectStateArray.add(nextStateArray.get(i));

							// REMOVE
							}else if(option.startsWith("remove;")) {
								if(judgeCondition(mCommands[1])) {
									nextStateArray.remove(i);
									i--;
								}
							}
						}

						if(selectStateArray.size()>0)
							nextStateArray = selectStateArray;

						long seed = System.currentTimeMillis();
						Random rand = new Random(seed);
						int flag = rand.nextInt(nextStateArray.size());
						performTransition = nextStateArray.get(flag);

						// if possible, want to refer to "MEMORY"

					}else if(nextStateArray.size()==1) {

						performTransition = nextStateArray.get(0);
					}



					if(performTransition!=null) { // perform state transition

						new Log("Read transition: " + performTransition);
						String nextState = performTransition.split(splitWord)[2];
						String activity = performTransition.split(splitWord)[3];
						String option = performTransition.split(splitWord)[4];



						//MM.Add("short_memory.txt", "activity_history", activity);
						//AM.addCommand(activity);

						CTU8.sendMsg("am;" + activity);

						currentState = nextContorol(nextState);

						//new Log("[STATE CHANGE] currentState: " + currentState);
						//		+ ", activity: " + activity
						//		+ ", option: " + option
						//		+ ", stateCommand: " + nextState);

						// prepare next transition

					}
				}else {

				}
			}

		}else {

			reconnect_count++;
			new Log("wait for re-connecting: " + ((reconnect_count*reconnect_count/2.0)+1) + " second");
			T.wait(((reconnect_count*reconnect_count/2)+1)*1000);
		}

	}


	public String nextContorol(String nextState) {

		//currentState = nextState;
		String preState = currentState;

		if(nextState.startsWith("+")||nextState.startsWith("-")) {

			String nextControlCommnads[] = nextState.split("&");

			for(int i=0; i<nextControlCommnads.length; i++) {

				if(nextControlCommnads[i].startsWith("+")) {

					String tempStr[] = currentState.split("&");
					boolean included = false;
					String state = nextControlCommnads[i].substring(1);

					for(int j=0; j<tempStr.length; j++) {
						if(tempStr[j].equals(state))
							included = true;
					}

					if(!included) {
						currentState = currentState + "&" + state;
					}

				}else if(nextControlCommnads[i].startsWith("-")) {

					currentState = currentState.replace(nextControlCommnads[i].substring(1) + "&", "");
					currentState = currentState.replace("&" + nextControlCommnads[i].substring(1), "");

				}

			}
		}else if(nextState.startsWith("ifMemory")) {





		}else{

			currentState = nextState;

		}


		if(!currentState.equals(preState)) {
			new Log("[STATE CHANGE] currentState: " + currentState + " (from " + preState + ")");
		}

		return currentState;
	}



	public void readScenario(String scenarioFile, String currentState) {

		ArrayList<String> transitions = new ArrayList<String>();

		ReadFile_sh(scenarioFile, transitions);

		transitionArray.clear();

		String[] states = (currentState+"&all").split("&");
		int count = 0;

		for(int j=0; j<states.length;j++) {

			for(int i=0; i<transitions.size(); i++) {

				String[] transitionCode = transitions.get(i).split(splitWord);

				if(transitionCode[0].equals(states[j])) {

					ArrayList<String> nextStateArray = collectCodes(transitionCode[1]);


					transitionArray.add(transitions.get(i));
					//if(nextStateArray.size()<1)
					//	transitionArray.add(transitionCode[0] + splitWord + transitionCode[1] + splitWord + transitionCode[2] + splitWord + transitionCode[3] + splitWord +  transitionCode[4]);
					//else {
					//	count++;
					//	transitionArray.add(transitionCode[0] + splitWord + transitionCode[1] + count + splitWord + transitionCode[2] + splitWord + transitionCode[3] + splitWord +  transitionCode[4]);
					//}

					//new Log(transitions.get(i));
				}

			}
		}


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

		MemoryManager MM = new MemoryManager();

		if(condition.equals("true"))
			return true;
		if(condition.equals("false"))
			return false;


		// condition = **.txt:ifContains()=**,**
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






	public void optionHandler(String command) {





	}


	public void sendLater(String command) {




	}


	public ArrayList<String> collectCodes(String word) {

		ArrayList<String> returnArray = new ArrayList<String>();

		for(int i=0; i<transitionArray.size(); i++) {

			if(transitionArray.get(i).split(splitWord)[1].equals(word)) {

				returnArray.add(transitionArray.get(i));

				//}else if(transitionArray.get(i).startsWith(word)) {

				//	String remain = transitionArray.get(i).split(splitWord)[1].replaceFirst(word, "");
				//	try {
					//		int num = Integer.parseInt(remain);
					//		returnArray.add(transitionArray.get(i));
					//	}catch(NumberFormatException e){

					//	}
			}else{

				String strs1[] = transitionArray.get(i).split(splitWord)[1].split("&");
				String strs2[] = word.split("&");

				if(strs1.length == strs2.length && strs2.length>1) {

					boolean same = true;

					for(int j=0; j<strs1.length; j++) {

						if(!word.contains(strs1[j]))
							same = false;
					}

					if(same)
						returnArray.add(transitionArray.get(i));

				}

			}

		}

		if(returnArray.size()==0) {


			String strs2[] = word.split("&");

			for(int k=1; k<strs2.length; k++) {

				for(int i=0; i<transitionArray.size(); i++) {

					if(transitionArray.get(i).split(splitWord)[1].equals("callsota"))
						continue;

					String strs1[] = transitionArray.get(i).split(splitWord)[1].split("&");

					if(strs2.length - strs1.length == k) {

						boolean same = true;

						for(int j=0; j<strs1.length; j++) {

							if(!word.contains(strs1[j]))
								same = false;
						}

						if(same)
							returnArray.add(transitionArray.get(i));

					}
				}

				if(returnArray.size() != 0)
					return returnArray;
			}
		}


		if(returnArray.size()==0) {
			for(int i=0; i<transitionArray.size(); i++) {
				if(transitionArray.get(i).split(splitWord)[1].equals("otherwise")) {
					returnArray.add(transitionArray.get(i));
				}
			}
		}

		return returnArray;
	}


	public class Timer extends Thread{

		int time = 0;
		boolean running = true;
		boolean stop = false;


		public void run() {

			while(running) {

				T.wait(1000);
				if(!stop)
					time++;

			}
		}

		public int getTime(){

			return time;

		}

		public void endTime() {

			running = false;
		}

		public void resetTime() {

			time = 0;

		}

		public void stopTime() {

			stop = true;
		}

		public void restartTime() {

			stop = false;
		}


	}

	public boolean ReadFile_sh(String FileName, ArrayList<String> array){ // From text content Put in array ///////////////////////////

		File file = new File(FileName);

		try{
			String str;
			BufferedReader br;

			// Confirm avaival And open buffer ////////////////////////
			if (IsAvailable(file))
				br = new BufferedReader(new FileReader(file));
			else
				return false;

			// Read each line in the file /////////////////////////
			new Log("read " + file.getName(),false,4);
			{
				while ((str = br.readLine()) != null) {
					if(!str.startsWith("#") && str.split(splitWord).length==5)
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

}
