import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import util.Log;

public class MemoryManager{

	String FILEDIR = "memories";
	String splitWord = " = ";
	String splitWord2 = ",";

	public MemoryManager(){

		File activities = new File(FILEDIR);
		if(activities.mkdir()){
			new Log("Make NEW Directly " + FILEDIR);

			File temp = new File(FILEDIR + "/"+ "test.txt");
			try {
				temp.createNewFile();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}


	//  change or add Config File  /////////////////////
	public boolean Add(String fileName, String key, Object _content){

		File file  = new File(FILEDIR + "/" + fileName);

		try {
			if(file.createNewFile()){
				new Log("Make NEW File '" + file.getName() + "'");
			}
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		ArrayList<String> memoryArray = new ArrayList<String>();
		ReadFile(file,memoryArray);

		int i;
		FileOutputStream fos;
		PrintWriter pw;
		String content = String.valueOf(_content);
		String str = "";

		//  Find out place of name  ////////////////////////////
		for(i=0;i<memoryArray.size();i++){
			str = memoryArray.get(i);
			if(str.startsWith(key + splitWord))
				break;
		}

		//  Change or Add config array  /////////////////////////

		if(i==memoryArray.size()){

			memoryArray.add(key + splitWord + content);
			new Log("add new key and memory: " + key + splitWord + content + " in " + file.getName());

		}else{

			String str2 = str.replaceFirst(key + splitWord, "");
			String str2s[] = str2.split(splitWord2);

			for(int j=0; j<str2s.length; j++) {

				if(str2s[j].equals(content)) {

					//new Log("already contain " + content + " @ " + key);
					new Log("already contain: " +  content + " at " + key + " in " + file.getName(), false);
					return false;
				}
			}

			if(str.endsWith(splitWord))
				str += content;
			else
				str+= splitWord2 + content;
			memoryArray.set(i, str);

			//new Log("add new memory: " + content + " @ " + key);
			new Log("add new memory: " + key + " += " + content + " in " + file.getName());


		}

		//  Write in file from config array  /////////////////////////
		try{
			if (IsAvailable(file)) {
				fos = new FileOutputStream(file);
				pw = new PrintWriter(fos);
				for(i=0;i<memoryArray.size();i++){
					str = String.valueOf(memoryArray.get(i));
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




	public String Read(String fileName, String key){

		File file  = new File(FILEDIR + "/" + fileName);

		ArrayList<String> memoryArray = new ArrayList<String>();
		ReadFile(file,memoryArray);

		int i;
		String str = "";

		//  Find out the place of String name  ////////////////////////////
		for(i=0;i<memoryArray.size();i++){
			str = memoryArray.get(i);
			if(str.startsWith(key + splitWord))
				break;
		}

		//  Return result in finding  /////////////////////////
		if(i==memoryArray.size()){
			new Log("Could Not find '" + key + "' in " + file.getName());
			return null;
		}else{
			new Log("read memory: " + str, false);
			String str2 = str.replaceFirst(key + splitWord, "");

			if(str2.equals(""))
				return null;
			else
				return str2;
		}
	}




	public boolean Clear(String fileName, String key){

		File file  = new File(FILEDIR + "/" + fileName);

		ArrayList<String> memoryArray = new ArrayList<String>();
		ReadFile(file,memoryArray);

		int i;
		String str = "";

		//  Find out the place of String name  ////////////////////////////
		for(i=0;i<memoryArray.size();i++){
			str = memoryArray.get(i);
			if(str.startsWith(key + splitWord))
				break;
		}

		//  Return result in finding  /////////////////////////
		if(i==memoryArray.size()){
			new Log("Could Not find '" + key + "' in " + file.getName());
			return false;
		}else{

			FileOutputStream fos;
			PrintWriter pw;

			new Log("clear memory: " + str);
			str = key + splitWord;
			memoryArray.set(i, str);

			try{
				if (IsAvailable(file)) {
					fos = new FileOutputStream(file);
					pw = new PrintWriter(fos);
					for(i=0;i<memoryArray.size();i++){
						str = memoryArray.get(i);
						pw.println(str);
					}
					pw.close();
					return true;
				}
			} catch (IOException err) {
				System.out.println(err);
			}


			return true;
		}
	}


	public boolean Clear(String fileName){

		File file  = new File(FILEDIR + "/" + fileName);

		new Log("Delete File '" + file.getName());
		file.delete();

		return true;

	}


	public boolean Contain(String fileName, String key, Object _content){

		File file  = new File(FILEDIR + "/" + fileName);

		ArrayList<String> memoryArray = new ArrayList<String>();
		if(!ReadFile(file,memoryArray))
			return false;

		int i;
		FileOutputStream fos;
		PrintWriter pw;
		String content = String.valueOf(_content);
		String str = "";

		//  Find out place of name  ////////////////////////////
		for(i=0;i<memoryArray.size();i++){
			str = memoryArray.get(i);
			if(str.startsWith(key + splitWord))
				break;
		}

		//  Change or Add config array  /////////////////////////

		if(i==memoryArray.size()){

			new Log("Could Not find '" + key + "' in " + file.getName(), false);
			return false;

		}else{

			String str2 = str.replaceFirst(key + splitWord, "");
			String str2s[] = str2.split(splitWord2);

			for(int j=0; j<str2s.length; j++) {

				if(str2s[j].equals(content)) {

					new Log("Contained!: " +  content + " at " + key + " in " + file.getName(), false);
					return true;
				}
			}
		}
		new Log("Not Contained!: " +  content + " at " + key + " in " + file.getName(), false);
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


}
