package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//	Log : a class outputting a log file
//		Log() -> initializing
//
//  How to use
//    Just write "new Log('TEXT')"
//	You can decide detail for each log
//		Log(Sting msg, boolean systemOut, int inDeep)
//			msg -> log message
//			sysemOut -> whether systemOut or Not
//			inDeep -> deep of track process
//
//  Able to change
//    LOGDIR -> a log directly
//    LOGFILE -> a log file name
//    deep -> how deep location it writes
//    maxCurrentNum -> max number of a log file stock
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Log extends FileManage{

	final protected static Object lock = new Object();

	static String LOGDIR = "log";
	static String LOGFILE_init = "_starting_record";
	static String LOGFILE = "javalog";
	static int currentNum = 0;
	static int maxCurrentNum = 20;
	static File file = new File(LOGDIR + "\\" + LOGFILE + LOGFILE_init +  ".log");
	static int eDeep = 2;
	static int sDeep = 2;
	static boolean D = true; // if you use not only LOG also SYSTEMOUT

	static boolean initializing = false;
	static boolean initialized = false;

	Tools T = new Tools();


	public Log(){

		//if(!init())
			//new Warning("Can't initialize 'Log.java'",1);
	}


	public Log(int inDeep){

		//if(!init())
			//new Warning("Can't initialize 'Log.java'",1);

		eDeep = inDeep;
	}


	private boolean init(){

		if(initialized  || initializing)
			return true;

		synchronized (lock){

			if(initialized  || initializing)
				return true;

			initializing = true;

			try{
					File newdir = new File(LOGDIR);
					if(newdir.mkdir()){
						new Log("Make NEW Directly 'log'",true,2,2);
					}

					if(file.createNewFile()){
						new Log("Make NEW File '" + file.getName() + "'",true,2,2);
					}

					File logNum = new File(LOGDIR + "\\" +"logNum" + ".log");
					if (logNum.createNewFile()) {
						new Log("Make New File '" + logNum.getName() + "'",true,2,2);
					}
					new Log("@@@ RUN STAMP @@@ -- " + System.getProperty("user.name"),false);

					int i;
					String str = ReadFile(logNum);
					if(str==null)
						currentNum = 0;
					else{
						i = Integer.parseInt(str.trim());
						if(i>maxCurrentNum)
							currentNum = 0;
						else
							currentNum = i;
						}


					WriteFile(logNum,String.valueOf(currentNum+1));

					file  = new File(LOGDIR + "\\" + LOGFILE + currentNum + ".log");
					file.delete();
					file.createNewFile();

			} catch (IOException e) {
					new Error("log initialize error" + e);
					return false;
			}


			initializing = false;
			initialized = true;

			new Log("@@@ RUN STAMP @@@ -- " + System.getProperty("user.name"),false);
			//new Log("@@@ START LOG @@@",false);
		}

		return true;
	}


    public Log(String msg){

    	new Log(msg, true, sDeep, eDeep);

    }


    public Log(String msg, int endDeep){

    	new Log(msg, true, sDeep, eDeep + endDeep);

    }


    public Log(String msg, int startDeep, int endDeep){

    	new Log(msg, true, startDeep, endDeep);

    }

    //// Below is for those who do NOT wnat to use systemoout
    public Log(String msg, boolean systemOut){

    	new Log(msg, systemOut, sDeep, eDeep);

     }


    public Log(String msg, boolean systemOut, int inDeep){

    	new Log(msg, systemOut, sDeep, eDeep+inDeep);

    }


    public Log(String msg, boolean systemOut, int startDeep, int endDeep){

    	Throwable trace = new Throwable();

    	Log_Thread LT = new Log_Thread(msg, systemOut, startDeep, endDeep, trace);
    	LT.start();
    }



    private class  Log_Thread extends Thread{

    	String msg; boolean systemOut; int startDeep; int endDeep; Throwable trace;

    	Log_Thread(String msg, boolean systemOut, int startDeep, int endDeep, Throwable trace){

    		this.msg = msg;
    		this.systemOut = systemOut;
    		this.startDeep = startDeep;
    		this.endDeep = endDeep;
    		this.trace = trace;

    	}

    	public void run(){

			if(!init())
				new Warning("Can't initialize 'Log.java'",1);

	    	if(startDeep>endDeep){
	    		new Warning("Log set ERORR startDeep > endDeep :" + startDeep + " > " + endDeep,2);
	    		startDeep = endDeep;
	    	}

	 		try{
				while(!IsAvailable(file)){
					System.out.println("wait to use a log file...");
					T.wait(1000);
				}

	 			FileOutputStream fos = new FileOutputStream(file,true);
	 			OutputStreamWriter osw = new OutputStreamWriter(fos);
	 			PrintWriter pw = new PrintWriter(osw);

	 			for(int i=startDeep; i<=endDeep; i++){

	 				if(i>=trace.getStackTrace().length)
	 					break;

	 				StackTraceElement element = trace.getStackTrace()[i];
	 				String className = element.getClassName();
	 				String methodName = element.getMethodName();
	 				int lineNumber = element.getLineNumber();
	 				Date date = new Date();



					String log,log_S;
					if(i==startDeep){
						//log = date + ":" + className + "[" + lineNumber + "]/" + methodName + " -> " + msg ;
						//log_S = className + "[" + lineNumber + "]/" + methodName + " -> " + msg ;
						log = date + ": " + msg;
						log_S = "LOG: " + msg;
					}
					else{
						log = log_S = " -> " + className + "[" + lineNumber + "]/" + methodName;
					}

	 				if(systemOut){
	 					System.out.println(log_S);
	 					pw.println(log);
	 				}else{
	 					pw.println(log);
	 				}
	 			}
	 			pw.close();

			} catch (IOException e) {
				System.out.println(e);
	 		} catch (ArrayIndexOutOfBoundsException e){
	 			System.out.println(e);
	 		}
    	}
     }


    private Log(Throwable error){

		try{
			FileOutputStream fos = new FileOutputStream(file,true);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			PrintWriter pw = new PrintWriter(osw);

			Throwable t = new Throwable();
			StackTraceElement element = t.getStackTrace()[1];
			String className = element.getClassName();
			String methodName = element.getMethodName();
			int lineNumber = element.getLineNumber();
			Date date = new Date();

			String log;
			log = date + ":" + className + "[" + lineNumber + "]/" + methodName + " -> " + error ;

			//System.out.println(log);
			pw.println(log);
			pw.close();

		} catch (IOException e) {
			System.out.println(e);
			//new Log(e);
 		} catch (ArrayIndexOutOfBoundsException e){
 			System.out.println(e);
 			//new Log(e);
 		}
    }
}



