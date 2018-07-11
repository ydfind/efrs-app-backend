package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
/**
 * 对PC返回结果，data中约定字段进行前置
 * @author kfzx-dengrd
 *
 */
public class ResultTopService {
	// 字段前置处理
	public static void topJsonKeys(BaseAppResultEntity appResult, String serviceKey){
	 	if(appResult.getJsonTData() == null ||
	 			serviceKey == null || serviceKey.equals(""))
    		return;
	 	ArrayList<String> keys = null;
    	ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
    	if(objFile != null)
    		keys = objFile.getAsTopKeys();
    	if((keys != null) && (keys.size() > 0)){// 仅存在需要前置的字段时，才进行前置
    		topJsonKeys(appResult.getJsonTData(), keys, 1);
    	}
	}
	
	public static void topJsonKeys(Object obj, ArrayList<String> keys, int level){
		if(obj instanceof JSONObject){
			topJsonKeys((JSONObject)obj, keys, level);
		}else if(obj instanceof JSONArray){
			topJsonKeys((JSONArray)obj, keys, level);
		}else{
			ExceptionService.throwCodeException("前置失败，无法识别该key格式0");
		}
	}
	
	public static void topJsonKeys(JSONObject obj, ArrayList<String> keys, int level){
		Set<String> Keys = obj.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: Keys){ 
			tempkeys.add(key);
		}
	
		Object objValue;
		for(String key: tempkeys) {
			objValue = obj.get(key);
			if(objValue instanceof JSONObject){
				topJsonKeys((JSONObject)objValue, keys, level + 1);
			}else if(objValue instanceof JSONArray){
				topJsonKeys((JSONArray)objValue, keys, level + 1);
			}
		}	
		final ArrayList<String> finalKeys = new ArrayList<String>();
		for(int i = 0; i < keys.size(); i++){
			finalKeys.add(keys.get(i));
		}
		if((obj.size() > 1) && (keys.size() > 0)){
			Map<String, Object> objMap = obj.getInnerMap();
			if(objMap instanceof LinkedHashMap){
				LinkedHashMap objLinked = (LinkedHashMap)objMap;
				// 排序
				ArrayList<Map.Entry<String, Object>> list = 
					new ArrayList<Map.Entry<String, Object>>(objLinked.entrySet());
				Collections.sort(list, new Comparator<Map.Entry<String, Object>>(){

					@Override
					public int compare(Entry<String, Object> arg0,
							Entry<String, Object> arg1) {
						// TODO Auto-generated method stub
						int leftVal = finalKeys.indexOf(arg0.getKey());
						int rightVal = finalKeys.indexOf(arg1.getKey());
						if(leftVal < 0){
							leftVal = 10000;
						}
						if(rightVal < 0)
							rightVal = 10000;
						return leftVal - rightVal;
					}
				});
//				if(level == 2){
//					LoggerAspect.logInfo(level + "-----start------------------------------");
//				}
				for(Map.Entry<String, Object> entry: list){
					objLinked.remove(entry.getKey());
					objLinked.put(entry.getKey(), entry.getValue());
//					if(level == 2){
//						LoggerAspect.logInfo(level + "-----aaaa---------" +entry.getKey() + "---------------------");
//					}
				}
//				if(level == 2){
//					LoggerAspect.logInfo(level + "-----end------------------------------");
//				}
				
			}else{
				ExceptionService.throwCodeException("JSONObject不是LinkedHashMap类型无法排序=" + 
						obj.getClass() + "--" + objMap.getClass() + "--");
			}
		}
	}
	
	public static void topJsonKeys(JSONArray obj, ArrayList<String> keys, int level){
		Object objValue;
    	for(int i = obj.size() - 1; i >= 0; i--){
			objValue = obj.get(i); 
    		if(objValue instanceof JSONObject){
    			topJsonKeys((JSONObject)objValue, keys, level + 1);
			}else if(objValue instanceof JSONArray){
				topJsonKeys((JSONArray)objValue, keys, level + 1);
			}else{
				ExceptionService.throwCodeException("前置失败，无法识别该key格式1");
			}
    	}
	}

}
