package com.icbc.efrs.app.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.service.ExceptionService;
/*
 * App请求结果翻译对应的file文件类
 */
public class TransFileEntity {
	private static String splitStr = "-";
	private String filename;// 文件名称
//	private ReqIntfEnums reqType;
	private Map<String, String> keyMap;
	private String reqKey;  // 请求的key
	
	public TransFileEntity(String filename){
		this.filename = filename;
		init();
	}
	
	public TransFileEntity(JSONObject jsonObject){
		keyMap = getJsonKeyMapByJson(jsonObject);
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
//			// 请求的接口类型
//			int reqTypeId = Integer.parseInt(strs[1]);
//			for(ReqIntfEnums e: ReqIntfEnums.values())
//				if(e.getID() == reqTypeId){
//					setReqType(e);
//					break;
//				}
//			if(getReqType() == ReqIntfEnums.ErrIntf){
//				throw new Exception("解析错误");
//			}
			
		}catch(Exception e){
			ExceptionService.throwCodeException("错误的翻译文件名：" + filename);
		}
		
		File file = new File(getFilename());
		if(!file.exists())
		{
			ExceptionService.throwCodeException("翻译文件无法识别");
		}
		
		setKeyMap(getJsonKeyMap());	
	}
	
	private Map<String, String> getJsonKeyMap(){
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
	            if (!tempString.startsWith("#") && (tempString.indexOf("=") > 0)) {
	                String[] strArray = tempString.split("=");
	                if(strArray.length != 2 ||strArray[0].equalsIgnoreCase("") || strArray[1].equalsIgnoreCase("")){
	                	ExceptionService.throwCodeException("翻译文件错误！filename = " + filename + "; " + strArray[0] + "-----" + strArray[1] + ";");
	                }
	                map.put(strArray[0], strArray[1]);
	            }
	            line++;
	        }
	        reader.close();
	    } catch (IOException e) {
	    	ExceptionService.throwCodeException("初始化翻译文件错误:" + filename);
	    } finally {
	        if (reader != null) {
	        	try {
	                reader.close();
	            } catch (IOException e1) {
	            }
	        }
	    }
//	    for (Map.Entry entry : map.entrySet()) {
//	    	LoggerAspect.logInfo(entry.getKey() + "=" + entry.getValue());
//	    }
	    return map;
	}

	public Map<String, String> getJsonKeyMapByJson(JSONObject jsonObject){
	    Map<String, String> map = new HashMap<String, String>();
	    if(jsonObject.containsKey(Constants.TRANS_FX_NAME_DATA)){
	    	JSONObject json = jsonObject.getJSONObject(Constants.TRANS_FX_NAME_DATA);
	    	InitJsonKeyMap(json, map);
	    }
		return map;
	}
	
	private void InitJsonKeyMap(JSONObject jsonObject, Map<String, String> map){
		Set<String> keys = jsonObject.keySet();
		for(String key: keys) {
			Object value = jsonObject.get(key);
			if(value instanceof JSONObject){
				InitJsonKeyMap((JSONObject)value, map);
			}
			else if(value instanceof String){
				String val = (String)value;
				map.put(key, val);
			}
			else{
			    ExceptionService.throwCodeException("风险翻译不能识别该类型的节点");
			}
		}
			
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

//	public void setReqType(ReqIntfEnums reqType) {
//		this.reqType = reqType;
//	}
//
//	public ReqIntfEnums getReqType() {
//		return reqType;
//	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

}
