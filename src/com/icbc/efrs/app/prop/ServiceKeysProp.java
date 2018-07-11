package com.icbc.efrs.app.prop;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.CoreUtils;
import com.icbc.efrs.app.utils.FileUtil;

public class ServiceKeysProp {
	private static final String cfgName = "json/";
	private static final String serviceKeyFileName = "servicekeys.json";
	public static Map<String, String> serviceKeyMap;
	
	static{
		init();
	}
	
	public static void init(){
		serviceKeyMap = new HashMap<String, String>();
		// serviceKeys的对照文件
		String keysFilename = getPath() + serviceKeyFileName;
		String ret = FileUtil.getContent(keysFilename);
		if (CoreUtils.nullSafeSize(ret) == 0) {
			ExceptionService.throwCodeException("无法找到对照文件 = " + keysFilename);
		}
		else{
			JSONObject jsonKeys = JSON.parseObject(ret, Feature.OrderedField);
			for(String key: jsonKeys.keySet()){
				if(serviceKeyMap.containsKey(key)){
					ExceptionService.throwCodeException("servicekeys配置文件key冲突 = " + key);
				}else{
					serviceKeyMap.put(key, jsonKeys.getString(key));
				}
			}
		}
	}
	
	public static String getServiceKey(String key){
		String result = key;
		if(serviceKeyMap.containsKey(key)){
			result = serviceKeyMap.get(key);
			LoggerAspect.logInfo("Servicekey change：" + key + " -> " + result);
		}
		return result;
	}
	
	private static String getPath(){
		String dir = ServerProp.getAppServerPath() + cfgName;
		LoggerAspect.logInfo("请求json的目录名称为：" + dir);
		return dir;
	}
	
//	public static void changeFileName(String filename){
//		String result = filename;
//		int temp = filename.lastIndexOf("/");
//		int lastindex = filename.lastIndexOf("\\");
//		if(lastindex < temp)
//			lastindex = temp;
//		String filepath = filename.substring(0, lastindex + 1);
//		String name = filename.substring(lastindex + 1);
//		String[] strs = name.split("-");
//		if(strs[0] != null && !strs[0].equals("") && serviceKeyMap.containsValue(strs[0])){
//			for(Map.Entry<String, String> entry: serviceKeyMap.entrySet()){
//				if(entry.getValue().equals(strs[0])){
//					String resFilename = entry.getKey();
//					name = name.replaceFirst(strs[0], resFilename);
//					result = filepath + name;
//					if(!filename.equals(result)){
//						System.out.println(filename + " ->  " + result);
//						File file = new File(filename);
//						file.renameTo(new File(result));
//					}
//					break;
//				}
//			}
//		}
//	}

}
