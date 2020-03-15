package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import Util.FileManage;

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  ConfigManage extends FileManage : Can Manage Config File
//	  ConfigManage(String config) : read or make "(String config).txt" and save the content in configArray
//    FileManage : basic function to manage file
//
//  Functions : Set,Output
//    boolean Set(String name,Object content) : add content to config at the name , return succeeded or failed
//    String Output(String name) : read the content at the name , return the content
//
//  Others
//    WarningMessage : display the warning
//    how to write in "(String config).txt"  -->  (name) = (content)
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Config extends FileManage{

	//  Global Variable  ////////////////////////////////////
	private File file;
	public ArrayList configArray;
	private String FILEDIR = "conf\\";
	private Tools T = new Tools();


	public Config(String configName){

		if(!init(configName))
			new Warning("Can't initialize 'Config.java'",1);

	}


	public Config(String configName, String folder){

		String str = folder.substring(folder.length()-2);
		if(str=="\\"){
			FILEDIR = folder;
		}else{
			new Warning("config foloder name error : " + folder);
			FILEDIR = folder + "\\";
		}

		if(!init(configName))
			new Warning("Can't initialize 'Config.java'",1);
	}


	private boolean init(String name){

		// Make directly having a config
		File newdir = new File(FILEDIR);
		if(newdir.mkdir())
			new Log("Make NEW Directly 'conf'");


		//  Make config file  ///////////////////////////////
		file  = new File(FILEDIR + name);
		try{
			if (!IsAvailable(file)) {
				file.createNewFile();
				new Log("Make New config : " + file.getName());
			}

		//  Read config  //////////////////////////////////

			configArray = new ArrayList();
			ReadFile(file,configArray);

		} catch (IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}


	//  change or add Config File  /////////////////////
	public boolean Set(String key,Object content){

		int i;
		String str;
		FileOutputStream fos;
		PrintWriter pw;

		//  Find out place of name  ////////////////////////////
		for(i=0;i<configArray.size();i++){
			str = String.valueOf(configArray.get(i));
			if(str.startsWith(key + " = "))
				break;
		}

		//  Change or Add config array  /////////////////////////
		str = String.valueOf(content);
		if(i==configArray.size()){
			configArray.add(key + " = " + str);
			new Log("add config " + key + " = " + str, true, 1,2);
		}else{
			str = key + " = " + str;
			if(str.equals(String.valueOf(configArray.get(i)))){
				new Log("Not change config " + str, false,1,2);
			}else{
				new Log("change config   FROM: " + String.valueOf(configArray.get(i)) + "   TO: " + str, 1,2);
				configArray.set(i,str);
			}
		}

		//  Write in file from config array  /////////////////////////
		try{
			if (IsAvailable(file)) {
				fos = new FileOutputStream(file);
				pw = new PrintWriter(fos);
				for(i=0;i<configArray.size();i++){
					str = String.valueOf(configArray.get(i));
					pw.println(str);
				}
				pw.close();
				return true;
			}
		} catch (IOException err) {
			System.out.println(err);
		}
		return false;
	}


	//  Read Config File content you want  /////////////////////////
	public String Get(String key){

		int i;
		String str = "null";

		//  Find out the place of String name  ////////////////////////////
		for(i=0;i<configArray.size();i++){
			str = String.valueOf(configArray.get(i));
			if(str.startsWith(key + " = "))
				break;
		}

		//  Return result in finding  /////////////////////////
		if(i==configArray.size()){
			new Warning("Could Not find '" + key + "' in " + file.getName());
			return null;
		}else{
			String str2[] = str.split(" = ");
			new Log("read config " + str,false);
			return str2[1].trim();
		}
	}


	public String Get(String key, String pre_content){

		String str = Get(key);
		if(str==null)
			str = pre_content;

		return str;
	}


	public int Get(String key, int pre_content){

		String str = Get(key);
		if(str==null)
			str = String.valueOf(pre_content);

		return T.Str2Int(str, pre_content);
	}


	public double Get(String key, double pre_content){

		String str = Get(key);
		if(str==null)
			str = String.valueOf(pre_content);

		return T.Str2Double(str, pre_content);
	}

}
