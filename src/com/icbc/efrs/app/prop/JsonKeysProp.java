package com.icbc.efrs.app.prop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonKeysProp {
	private static String filePath;// 文件相对路径
	private static final String cfgName = "config/keyTrans.cfg";
	
	public static Map<String, String> JsonKeyMap;
	
	static {
		init();
	}
	
	public static void init() {
		filePath = AppServerProp.getAppServerPath() + cfgName;
		
		File file = new File(filePath);
		if(!file.exists())
		{
			assert false: "CX：翻译配置文件无法找到！";
		}
		else
		{
			System.out.println("Trans file has found：" + filePath);
		}
		
		JsonKeyMap = getJsonKeyMap();	
	}
	
	public static Map<String, String> getJsonKeyMap(){
	    Map<String, String> map = new HashMap<String, String>();
	    File file = new File(filePath);
	    BufferedReader reader = null;
	    try {
	        reader = new BufferedReader(new FileReader(file));
	        String tempString = null;
	        int line = 1;
	        // 一次读入一行，直到读入null为文件结束
	        while ((tempString = reader.readLine()) != null) {
	            // 显示行号
	            System.out.println("line " + line + ": " + tempString);
	            if (!tempString.startsWith("#")) {
	                String[] strArray = tempString.split("=");
	                map.put(strArray[0], strArray[1]);
	            }
	            line++;
	        }
	        reader.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (reader != null) {
	        	try {
	                reader.close();
	            } catch (IOException e1) {
	            }
	        }
	    }
	    for (Map.Entry entry : map.entrySet()) {
	        System.out.println(entry.getKey() + "=" + entry.getValue());
	    }
	    return map;
	}

}
