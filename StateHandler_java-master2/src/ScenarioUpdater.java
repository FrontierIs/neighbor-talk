import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import util.FileManage;
import util.Log;
import util.Tools;

public class ScenarioUpdater extends Thread{

	Tools T = new Tools();
	MemoryManager MM = new MemoryManager();

	ScenarioUpdater(){


	}

	public void run() {

		// weather updater
		while(true) {

			update();

			T.wait(60000);
		}


	}

	public void update() {

		new Log("Scenario Update");

        File activities = new File("activities");
        File activitiesList[] = activities.listFiles();

        for(int j=0; j<activitiesList.length; j++) {

        	if(activitiesList[j].getName().startsWith("news")){

        		if(!activitiesList[j].getName().startsWith("news" + timeStamp1() )){

        			new Log("Delete news: " + activitiesList[j].getName());
        			activitiesList[j].delete();
        		}
        	}

        }
        makeActivity_news(getData("https://www3.nhk.or.jp/rss/news/cat0.xml"));

        makeActivity_weather(getData("http://weather.livedoor.com/forecast/webservice/json/v1?city=270000"));

        makeActivity_time();

	}



	public String getData(String _url) {

		URL url;
		String result = "";

		try {
			url = new URL(_url);

			HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
			InputStream in = httpConn.getInputStream();
			BufferedReader r = new BufferedReader(
			new InputStreamReader(in,"UTF-8"));


			for (;;) {
			String line = r.readLine();
			if (line == null) {
			break;
			}
			//System.out.println("\r\n"+line);

			result += line;
			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return result;


	}




	public void makeActivity_news(String rss){


		try {

			//String head = "scenarioInit\ntrackFace:true\nnonverbalListening:true\n";
			//String tail = "loadActivity:timeoutForReplyMode\n";

	        DocumentBuilderFactory  factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document  document = builder.parse(new InputSource(new ByteArrayInputStream(rss.getBytes("UTF-8"))));
	        //Document  document = builder.parse(rss);
	        Element root = document.getDocumentElement();

	        //NodeList channel = root.getElementsByTagName("channel");
            //NodeList title = ((Element)channel.item(0)).getElementsByTagName("title");

	        NodeList itemlist = root.getElementsByTagName("item");

	        for(int i =0; i<itemlist.getLength(); i++) {

	            NodeList title = ((Element)itemlist.item(i)).getElementsByTagName("title");
	            NodeList description = ((Element)itemlist.item(i)).getElementsByTagName("description");

	            File activities = new File("activities");
	            File activitiesList[] = activities.listFiles();

	            boolean contained = false;
	            int count = 0;

	            for(int j=0; j<activitiesList.length; j++) {

	            	if(activitiesList[j].getName().startsWith("news" + timeStamp1() )){

	            		FileManage FM = new FileManage();

		            	String saytitle = FM.ReadFile(activitiesList[j]);
		            	count++;

		            	if(saytitle.contains(title.item(0).getFirstChild().getNodeValue())) {
		            		contained = true;
		            		break;
		            	}

	            	//System.out.println(saytitle);
	            	}

	            }



	            if(!contained) {

	            	//System.out.println("hoge");

	            	FileManage FM = new FileManage();
	            	ArrayList<String> commands = new ArrayList<String>();
	            	commands.add("sota;addAction:sayWith:" + title.item(0).getFirstChild().getNodeValue());
	            	commands.add("sota;addAction:motion:raiseHand");
	            	commands.add("sota;addAction:wait:2000");
	            	for(int j=0; j<description.item(0).getFirstChild().getNodeValue().split("。").length; j++) {
	            		commands.add("sota;addAction:motionWith:unazuki");
	            		commands.add("sota;addAction:say:" + description.item(0).getFirstChild().getNodeValue().split("。")[j]);
	            	}

	            	String str = timeStamp1() + count;
	            	commands.add("addMemory:long_memory.txt:reported_news=news" + str);
	            	FM.MakeFile("activities/news" + str + ".txt", commands);
	            	//System.out.println(title.item(0).getFirstChild().getNodeValue());

	            }

	        }


	        // FOR newnews.txt
	        File activities = new File("activities");
            File activitiesList[] = activities.listFiles();

            boolean newnews = false;
            //int count = 0;

            if(MM.Read("long_memory.txt", "newnews")==null)
            	MM.Add("long_memory.txt", "newnews", "true");

            FileManage FM = new FileManage();
        	ArrayList<String> commands = new ArrayList<String>();

            for(int j=0; j<activitiesList.length; j++) {
            	if(activitiesList[j].getName().startsWith("news")){

            		MemoryManager MM = new MemoryManager();
            		String reportedNews = MM.Read("long_memory.txt", "reported_news");

            		if(reportedNews!=null)
            		if(!reportedNews.contains(activitiesList[j].getName().replace(".txt", ""))) {
            			//count++;

    	            	commands.add("sota;addAction:wait:2000");
    	            	commands.add("loadActivity:" + activitiesList[j].getName().replace(".txt", ""));
            			//System.out.println(reportedNews);
            			//System.out.println(activitiesList[j].getName().replace(".txt", ""));

    	            	if(!newnews)
    	                	FM.MakeFile("activities/newnews_one.txt", commands);

    	            	newnews = true;
            		}
            	}
            }

            //FileManage FM = new FileManage();
        	//ArrayList<String> commands = new ArrayList<String>();
        	//commands.add(String.valueOf(count));
        	//FM.MakeFile("activities/num_news.txt", commands);

            if(newnews) {



            	//FileManage FM = new FileManage();
            	//ArrayList<String> commands = new ArrayList<String>();
            	//commands.add("addAction:motionWith:raiseHand");
            	//commands.add("addAction:say:新着のニュースが" + count + "件あるみたいです。");

            	//FM.MakeFile("activities/newnews.txt");

        		if(MM.Contain("long_memory.txt","newnews","false")) {
        			MM.Clear("long_memory.txt", "newnews");
        			MM.Add("long_memory.txt", "newnews", "true");
        		}
        		//if(!MM.Contain("long_memory.txt","newnews_count",count)) {
        		//	MM.Clear("long_memory.txt", "newnews_count");
        		//	MM.Add("long_memory.txt", "newnews_count", count);
        		//}
            }else {



        		if(MM.Contain("long_memory.txt","newnews","true")) {
        			MM.Clear("long_memory.txt", "newnews");
        			MM.Add("long_memory.txt", "newnews", "false");
        		}

            }

        	FM.MakeFile("activities/newnews.txt", commands);






		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}







	}




	public void makeActivity_weather(String json){

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		engine.put("json_data",json);

		//String head = "scenarioInit\ntrackFace:true\nnonverbalListening:true\n";
		//String tail = "loadActivity:timeoutForReplyMode\n";

		String head = "";
		String tail = "";

		String text0 = "sota;addAction:sayWith:ごめんなさい.\n"
				+ "sota;addAction:motion:ojigi\n"
				+ "sota;addAction:say:今日の天気はよくわからないです。\n";

		String text0t = "sota;addAction:sayWith:ごめんなさい.\n"
				+ "sota;addAction:motion:ojigi\n"
				+ "sota;addAction:say:今日の気温はよくわからないです。\n";

		String text1 = "sota;addAction:sayWith:ごめんなさい.\n"
				+ "sota;addAction:motion:ojigi\n"
				+ "sota;addAction:say:明日の天気はよくわからないです。\n";

		String text1t = "sota;addAction:sayWith:ごめんなさい.\n"
				+ "sota;addAction:motion:ojigi\n"
				+ "sota;addAction:say:明日の気温はよくわからないです。\n";

		String text2 = "sota;addAction:sayWith:ごめんなさい.\n"
				+ "sota;addAction:motion:ojigi\n"
				+ "sota;addAction:say:明後日の天気はよくわからないです。\n";

		String text2t = "sota;addAction:sayWith:ごめんなさい.\n"
				+ "sota;addAction:motion:ojigi\n"
				+ "sota;addAction:say:明後日の気温はよくわからないです。\n";


		try {

			engine.eval("var obj = JSON.parse(json_data)");
			engine.eval("var text = obj.description.text");
			engine.eval("var city = obj.location.city");

			String city = (String)engine.get("city");

			engine.eval("var dateLabel0 = obj.forecasts[0].dateLabel");
			engine.eval("var telop0 = obj.forecasts[0].telop");
			engine.eval("var date0 = obj.forecasts[0].date");
			engine.eval("var max0 = obj.forecasts[0].temperature.max");
			engine.eval("var min0 = obj.forecasts[0].temperature.min");
			engine.eval("if(max0 != null) max0 = obj.forecasts[0].temperature.max.celsius");
			engine.eval("if(min0 != null) min0 = obj.forecasts[0].temperature.min.celsius");

			String dateLabel0 = (String)engine.get("dateLabel0");
			String telop0 = (String)engine.get("telop0");
			String date0 = (String)engine.get("date0");
			String max0 = (String)engine.get("max0");
			String min0 = (String)engine.get("min0");



			if(dateLabel0!=null && telop0!=null && date0!=null) {

				text0 = "sota;addAction:say:" + dateLabel0 + "、" + date0.split("-")[1].replace("0", "") + "月"
						+ date0.split("-")[2].replace("0", "") + "日の、" + city + "の天気は、" + telop0 + "でしょう。\n";

				if(max0!=null) {

					text0 += "sota;addAction:say:最高気温は、" + max0 + "度";

					if(min0!=null)
						text0 += "、最低気温は、" + min0 + "度";

					text0 += "ぐらいでしょう。\n";
				}

			}

			if(max0!=null && min0!=null) {

				text0t =  "sota;addAction:say:" + dateLabel0 + "の、" + city + "の最高気温は、" + max0 + "度" + "、最低気温は、" + min0 + "度ぐらいでしょう。\n";
			}

			engine.eval("var dateLabel1 = obj.forecasts[1].dateLabel");
			engine.eval("var telop1 = obj.forecasts[1].telop");
			engine.eval("var date1 = obj.forecasts[1].date");
			engine.eval("var max1 = obj.forecasts[1].temperature.max");
			engine.eval("var min1 = obj.forecasts[1].temperature.min");
			engine.eval("if(max1 != null) max1 = obj.forecasts[1].temperature.max.celsius");
			engine.eval("if(min1 != null) min1 = obj.forecasts[1].temperature.min.celsius");

			String dateLabel1 = (String)engine.get("dateLabel1");
			String telop1 = (String)engine.get("telop1");
			String date1 = (String)engine.get("date1");
			String max1 = (String)engine.get("max1");
			String min1 = (String)engine.get("min1");

			if(dateLabel1!=null && telop1!=null && date1!=null) {

				text1 = "sota;addAction:say:" + dateLabel1 + "、" + date1.split("-")[1].replace("0", "") + "月"
						+ date1.split("-")[2].replace("0", "") + "日の、" + city + "の天気は、" + telop1 + "でしょう。\n";

				if(max1!=null) {

					text1 += "sota;addAction:say:最高気温は、" + max1 + "度";

					if(min1!=null)
						text1 += "、最低気温は、" + min1 + "度";

					text1 += "ぐらいでしょう。\n";
				}

			}

			if(max1!=null && min1!=null) {

				text1t =  "sota;addAction:say:" + dateLabel1 + "の、" + city + "の最高気温は、" + max1 + "度" + "、最低気温は、" + min1 + "度ぐらいでしょう。\n";
			}


			engine.eval("var dateLabel2 = obj.forecasts[2].dateLabel");
			engine.eval("var telop2 = obj.forecasts[2].telop");
			engine.eval("var date2 = obj.forecasts[2].date");
			engine.eval("var max2 = obj.forecasts[2].temperature.max");
			engine.eval("var min2 = obj.forecasts[2].temperature.min");
			engine.eval("if(max2 != null) max2 = obj.forecasts[2].temperature.max.celsius");
			engine.eval("if(min2 != null) min2 = obj.forecasts[2].temperature.min.celsius");

			String dateLabel2 = (String)engine.get("dateLabel2");
			String telop2 = (String)engine.get("telop2");
			String date2 = (String)engine.get("date2");
			String max2 = (String)engine.get("max2");
			String min2 = (String)engine.get("min2");



			if(dateLabel2!=null && telop2!=null && date2!=null) {

				text2 = "sota;addAction:say:" + dateLabel2 + "、" + date2.split("-")[1].replace("0", "") + "月"
						+ date2.split("-")[2].replace("0", "") + "日の、" + city + "の天気は、" + telop2 + "でしょう。\n";

				if(max2!=null) {

					text2 += "sota;addAction:say:最高気温は、" + max2 + "度";

					if(min2!=null)
						text2 += "、最低気温は、" + min2 + "度";

					text2 += "ぐらいでしょう。\n";
				}

			}

			if(max2!=null && min2!=null) {

				text2t =  "sota;addAction:say:" + dateLabel2 + "の、" + city + "の最高気温は、" + max2 + "度" + "、最低気温は、" + min2 + "度ぐらいでしょう。\n";
			}


		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			new Log("ERROR(weather): " + e.toString());
		}


		FileManage FM = new FileManage();

		ArrayList<String> array0 = new ArrayList<String>();
		for(int i=0; i<(head+text0+tail).split("\n").length; i++)
			array0.add((head+text0+tail).split("\n")[i]);
		FM.MakeFile("activities/sayweather_today.txt", array0);

		ArrayList<String> array0t = new ArrayList<String>();
		for(int i=0; i<(head+text0t+tail).split("\n").length; i++)
			array0t.add((head+text0t+tail).split("\n")[i]);
		FM.MakeFile("activities/saytemperature_today.txt", array0t);

		ArrayList<String> array1 = new ArrayList<String>();
		for(int i=0; i<(head+text1+tail).split("\n").length; i++)
			array1.add((head+text1+tail).split("\n")[i]);
		FM.MakeFile("activities/sayweather_tommorow.txt", array1);

		ArrayList<String> array1t = new ArrayList<String>();
		for(int i=0; i<(head+text1t+tail).split("\n").length; i++)
			array1t.add((head+text1t+tail).split("\n")[i]);
		FM.MakeFile("activities/saytemperature_tommorow.txt", array1t);

		ArrayList<String> array2 = new ArrayList<String>();
		for(int i=0; i<(head+text2+tail).split("\n").length; i++)
			array2.add((head+text2+tail).split("\n")[i]);
		FM.MakeFile("activities/sayweather_aftertommorow.txt", array2);

		ArrayList<String> array2t = new ArrayList<String>();
		for(int i=0; i<(head+text2t+tail).split("\n").length; i++)
			array2t.add((head+text2t+tail).split("\n")[i]);
		FM.MakeFile("activities/saytemperature_aftertommorow.txt", array2t);

	}



	public void makeActivity_time() {

		FileManage FM = new FileManage();
		Calendar now = Calendar.getInstance();

		// FOR time.txt
		String h = String.valueOf(now.get(now.HOUR_OF_DAY));
		String m = String.valueOf(now.get(now.MINUTE));

		ArrayList<String> array0 = new ArrayList<String>();
		array0.add("sota;addAction:say:現在の時刻は、" + h + "時" + m +"分ぐらいです");

		FM.MakeFile("activities/time.txt", array0);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

		// FOR date.txt

		ArrayList<String> array1 = new ArrayList<String>();
		array1.add("sota;addAction:say:今日は、" + sdf.format(now.getTime()) + "です");
		//array1.add("addAction:say:今日は、" + h + "年" + mo+1 + "月" + d + "日" + "です");

		FM.MakeFile("activities/date.txt", array1);

		// FOR memory.txt
		String y = String.valueOf(now.get(now.YEAR));
		String mo = String.valueOf(now.get(now.MONTH)+1);
		String d = String.valueOf(now.get(now.DAY_OF_MONTH));
		String w = String.valueOf(now.get(now.DAY_OF_WEEK));

		MemoryManager MM = new MemoryManager();
		if(!MM.Contain("long_memory.txt",  "year", y)) {
			MM.Clear("long_memory.txt", "year");
			MM.Add("long_memory.txt", "year", y);
		}
		if(!MM.Contain("long_memory.txt",  "month", mo)) {
			MM.Clear("long_memory.txt", "month");
			MM.Add("long_memory.txt", "month", mo);
		}
		if(!MM.Contain("long_memory.txt",  "day", d)) {
			MM.Clear("long_memory.txt", "day");
			MM.Add("long_memory.txt", "day", d);
		}
		if(!MM.Contain("long_memory.txt",  "day_of_week", w)) {
			MM.Clear("long_memory.txt", "day_of_week");
			MM.Add("long_memory.txt", "day_of_week", w);
		}
		if(!MM.Contain("long_memory.txt",  "hour", h)) {
			MM.Clear("long_memory.txt", "hour");
			MM.Add("long_memory.txt", "hour", h);
		}

	}


    String timeStamp1() {

    	//String splitWord = "\t";

		Calendar now = Calendar.getInstance();

		 String y = String.valueOf(now.get(now.YEAR));
		 String mo = String.valueOf(now.get(now.MONTH)+1);
		 String d = String.valueOf(now.get(now.DAY_OF_MONTH));
//		 String h = String.valueOf(now.get(now.HOUR_OF_DAY));
//		 String m = String.valueOf(now.get(now.MINUTE));
//		 String s = String.valueOf(now.get(now.SECOND));
//		 String ms = String.valueOf(now.get(now.MILLISECOND));


	     return  y + mo + d ;



    }


}
