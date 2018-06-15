package com.icbc.efrs.app.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.AppServerProp;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.FileUtil;

public class TransFileEntity {
	private static String splitStr = "-";
	private String filename;// 文件名称
	private ReqIntfEnums reqType;
	private Map<String, String> keyMap;
	private String reqKey;  // 请求的key
	
	public TransFileEntity(String filename){
		this.filename = filename;
		init();
	}
	
	public void init(){
		int lastindex, temp;
		temp = filename.lastIndexOf("/");
		lastindex = filename.lastIndexOf("\\");
		if(lastindex < temp)
			lastindex = temp;
		String[] strs = filename.substring(lastindex + 1).split(splitStr);
		try{
			if(strs.length != 3)
				throw new Exception("解析错误");
			// 请求的关键字
			reqKey = strs[0];
			// 请求的接口类型
			int reqTypeId = Integer.parseInt(strs[1]);
			for(ReqIntfEnums e: ReqIntfEnums.values())
				if(e.getID() == reqTypeId){
					setReqType(e);
					break;
				}
			if(getReqType() == ReqIntfEnums.ErrIntf){
				throw new Exception("解析错误");
			}
			
		}catch(Exception e){
			System.out.println("错误的翻译文件名：" + filename);
			e.printStackTrace();
		}
		
		File file = new File(getFilename());
		if(!file.exists())
		{
			ExceptionService.throwCodeException("翻译文件无法识别");
		}
		else
		{
			System.out.println("Trans file has found：" + getFilename());
		}
		
		setKeyMap(getJsonKeyMap());	
	}
	
	public Map<String, String> getJsonKeyMap(){
	    Map<String, String> map = new HashMap<String, String>();
	    File file = new File(getFilename());
	    BufferedReader reader = null;
	    try {
	    	InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
//	        reader = new BufferedReader(new FileReader(file));
	        reader = new BufferedReader(isr);
	        String tempString = null;
	        int line = 1;
	        // 一次读入一行，直到读入null为文件结束
	        while ((tempString = reader.readLine()) != null) {
	            // 显示行号
	            System.out.println("line " + line + ": " + tempString);
	            if (!tempString.startsWith("#") && (tempString.indexOf("=") > 0)) {
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

	
	public void setReqKey(String reqKey) {
		this.reqKey = reqKey;
	}

	public String getReqKey() {
		return reqKey;
	}

	public void setKeyMap(Map<String, String> keyMap) {
		this.keyMap = keyMap;
	}

	public Map<String, String> getKeyMap() {
		return this.keyMap;
	}

	public void setReqType(ReqIntfEnums reqType) {
		this.reqType = reqType;
	}

	public ReqIntfEnums getReqType() {
		return reqType;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

}