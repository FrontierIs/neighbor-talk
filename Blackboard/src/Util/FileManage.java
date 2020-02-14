package Util;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import java.util.ArrayList;
import java.util.Calendar;

public class FileManage {

	//public FileOutputStream fos;
	//public PrintWriter pw;

/*
	public FileManage(String name){
		// Make config file ///////////////////////////////
		File file  = new File(name);
		try{
			if (!IsAvailable(file)) {
				file.createNewFile();
				System.out.println("Make New config");
			}

		// Read config //////////////////////////////////
			ArrayList contentArray = new ArrayList();
			ReadFile(file,contentArray);

		} catch (IOException err) {
			System.out.println(err);
		}
	}

*/
	public boolean IsAvailable(File file){ // Let you know if the file is avaival /////////////////////
		if (file.exists()) {
			if (file.isFile() && file.canRead() && file.canWrite()) {
				return true;
			}
		}
		System.out.println(file.getPath() + " is Not avaival");
		return false;
	}


	public String ReadFile(File file){ // From text content Put in array ///////////////////////////
		try{
			String str;
			BufferedReader br;

			// Confirm avaival And open buffer ////////////////////////
			if (IsAvailable(file))
				br = new BufferedReader(new FileReader(file));
			else
				return null;

			// Read each line in the file /////////////////////////
			new Log("read " + file.getName(),false,3,3);

			str = br.readLine();
			br.close();
			return str;

		}catch (IOException err) {
			System.out.println(err);
		}
		return null;
	}


	public boolean ReadFile(File file,ArrayList array){ // From text content Put in array ///////////////////////////
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


	public boolean ReadFile(File file,ArrayList array, String code){ // From text content Put in array ///////////////////////////
		try{
			String str;
			BufferedReader br;

			// Confirm avaival And open buffer ////////////////////////
			if (IsAvailable(file))
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),code));
				//br = new BufferedReader(new FileReader(file));
			else
				return false;

			// Read each line in the file /////////////////////////
			new Log("read " + file.getName(),false,4);
			{
				while ((str = br.readLine()) != null) {
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


	public boolean ReadFile(String FileName,ArrayList array){ // From text content Put in array ///////////////////////////

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

	public boolean ReadFile(String FileName,ArrayList array, String code){ // From text content Put in array ///////////////////////////

		File file = new File(FileName);

		try{
			String str;
			BufferedReader br;

			// Confirm avaival And open buffer ////////////////////////
			BufferedReader BufferedReader;
			if (IsAvailable(file))
				br = new BufferedReader(new InputStreamReader(new FileInputStream(file),code));
				//br = new BufferedReader(new FileReader(file));
			else
				return false;

			// Read each line in the file /////////////////////////
			new Log("read " + file.getName(),false,4);
			{
				while ((str = br.readLine()) != null) {
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

	public boolean MakeFile(String name){

		File file = new File(name);

		try{
			if(file.createNewFile())
				new Log("Make New File : " + file.getAbsoluteFile(),true,2,2);

		} catch (IOException err) {
			new Error(name + ", " + err.toString());
			return false;
		}

		return true;
	}


	public boolean ClearFile(String name){

		File file = new File(name);

		if(!file.exists()){
			new Error(name + " doesn't exit.");
			return false;
		}

		if(!file.delete()){
			new Error(name + " doesn't delete.");
			return false;
		}

		if(!MakeFile(name))
			return false;

		return true;

	}


	public boolean MakeFile(String name, ArrayList data){

		File file = new File(name);

		try{
			if(file.createNewFile())
				new Log("Make New File : " + file.getAbsoluteFile(),true,2,2);

			FileWriter fw = new FileWriter(file);
			for(int i=0; i<data.size(); i++)
				fw.write((String)data.get(i) + "\r\n");
			fw.close();



		} catch (IOException err) {
			new Error(name + ", " + err.toString());
			return false;
		}

		return true;
	}


	public boolean MakeFile(String name, ArrayList data, String code){

		// "UTF-8"
		File file = new File(name);

		try{
			if(file.createNewFile())
				new Log("Make New File : " + file.getAbsoluteFile(),true,2,2);

			//FileWriter fw = new FileWriter(file);
			PrintWriter pw    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), code)));
			for(int i=0; i<data.size(); i++)
				pw.println((String)data.get(i));
				//fw.write((String)data.get(i) + "\r\n");
			pw.close();



		} catch (IOException err) {
			new Error(name + ", " + err.toString());
			return false;
		}

		return true;
	}

	public boolean MakeDir(String name){

		File newdir = new File(name);
		if(newdir.mkdir())
			new Log("Make NEW Directly '"+ name + "'",true,2,2);

		return true;
	}


	public boolean WriteFile(String filename, String str){

		try{

			File file = new File(filename);
			FileWriter fw = new FileWriter(file);
			fw.write(str);
			fw.close();

		}catch(IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}


	public boolean WriteFile(File file, String str){

		try{

			FileWriter fw = new FileWriter(file);
			fw.write(str);
			fw.close();

		}catch(IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}


	public boolean WriteFile(String filename, ArrayList array, String code){

		try{

			String str[] = TransArrayListToStr(array);
			File file = new File(filename);

			//FileWriter fw = new FileWriter(file,true);
			PrintWriter pw    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), code)));
			for(int i=0; i<str.length; i++)
				pw.write(str[i] + "\r\n");
			pw.close();

		}catch(IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}


	public boolean AddWriteFile(String filename, String str){

		try{

			File file = new File(filename);
			FileWriter fw = new FileWriter(file,true);
			fw.write(str);
			fw.close();

		}catch(IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}

	public boolean AddWriteFile(String filename, String str, String code){

		try{

			File file = new File(filename);
			//FileWriter fw = new FileWriter(file,true);
			PrintWriter pw    = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true), code)));
			pw.write(str);
			pw.close();

		}catch(IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}


	public boolean AddWriteFile(File file, String str){

		try{

			FileWriter fw = new FileWriter(file,true);
			fw.write(str);
			fw.close();

		}catch(IOException err) {
			System.out.println(err);
			return false;
		}

		return true;
	}


    public FilenameFilter getFileExtensionFilter(String extension) {
        final String _extension = extension;
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
                boolean ret = name.endsWith(_extension);
                return ret;
            }
        };
    }


    public FilenameFilter getFileStartsWithFilter(String text) {
        final String _text = text;
        return new FilenameFilter() {
            public boolean accept(File file, String name) {
                boolean ret = name.startsWith(_text);
                return ret;
            }
        };
    }


	public ArrayList TransToArrayList(String[] str){

		ArrayList tempArray = new ArrayList();
		int i;

		for(i=0;i<str.length;i++)
			tempArray.add(str[i]);

		return tempArray;
	}


	public ArrayList TransToArrayList(int[] integer){

		ArrayList tempArray = new ArrayList();
		int i;

		for(i=0;i<integer.length;i++)
			tempArray.add(String.valueOf(integer[i])+"\r\n");

		return tempArray;
	}


	public ArrayList TransToArrayList(double[] d){

		ArrayList tempArray = new ArrayList();
		int i;

		for(i=0;i<d.length;i++)
			tempArray.add(String.valueOf(d[i])+"\r\n");

		return tempArray;
	}


	public String[] TransArrayListToStr(ArrayList tempArray){

		int i;
		String[] str = new String[tempArray.size()];

		for(i=0;i<tempArray.size();i++)
			str[i] = String.valueOf(tempArray.get(i));

		return str;
	}


	public int[] TransArrayListToInt(ArrayList tempArray){

		int i;
		int[] integer = new int[tempArray.size()];

		for(i=0;i<tempArray.size();i++)
			integer[i] = Integer.parseInt(String.valueOf(tempArray.get(i)));

		return integer;
	}

	public String nowStr(){

		 Calendar now = Calendar.getInstance();

		 String y = String.valueOf(now.get(Calendar.YEAR));
		 String mo = String.valueOf(now.get(Calendar.MONTH));
		 String d = String.valueOf(now.get(Calendar.DATE));
		 String h = String.valueOf(now.get(now.HOUR_OF_DAY));
		 String m = String.valueOf(now.get(now.MINUTE));
	     if(Integer.parseInt(m)<10)
	    	 m = "0" + m;

	     return y + mo + d + h + m ;
	}

}