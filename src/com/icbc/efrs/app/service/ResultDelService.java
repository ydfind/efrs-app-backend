package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
/**
 * 对PC返回结果，对于约定不需要显示的字段，进行删除处理
 * @author kfzx-dengrd
 *
 */
public class ResultDelService {
	
	/**
	 * 对PC返回结果，对于约定不需要显示的字段，进行删除处理
	 * 
	 * @param appResult PC返回的结果对象
	 * @param keys 需要删除的key集合
	 */
	public static void delJsonKeys(BaseAppResultEntity appResult, ArrayList<String> keys){
	 	if(appResult.getJsonTData() == null)
    		return;
    	if((keys != null) && (keys.size() > 0)){// 仅存在需要前置的字段时，才进行前置
    		delJsonKeys(appResult.getJsonTData(), keys);
    	}
	}
	
	private static void delJsonKeys(Object obj, ArrayList<String> keys){
		if(obj instanceof JSONObject){
			delJsonKeys((JSONObject)obj, keys);
		}else if(obj instanceof JSONArray){
			delJsonKeys((JSONArray)obj, keys);
		}else{
			ExceptionService.throwCodeException("删除key失败，无法识别该key格式0");
		}
	}
	
	private static void delJsonKeys(JSONObject obj, ArrayList<String> keys){
		Set<String> Keys = obj.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: Keys){ 
			tempkeys.add(key);
		}
	
		Object objValue;
		for(String key: tempkeys) {
			objValue = obj.get(key);
			if(objValue instanceof JSONObject){
				delJsonKeys((JSONObject)objValue, keys);
			}else if(objValue instanceof JSONArray){
				delJsonKeys((JSONArray)objValue, keys);
			}else if(keys.contains(key)){
				obj.remove(key);
			}
		}		
	}
	
	private static void delJsonKeys(JSONArray obj, ArrayList<String> keys){
		Object objValue;
    	for(int i = obj.size() - 1; i >= 0; i--){
			objValue = obj.get(i); 
    		if(objValue instanceof JSONObject){
    			delJsonKeys((JSONObject)objValue, keys);
			}else if(objValue instanceof JSONArray){
				delJsonKeys((JSONArray)objValue, keys);
			}else{
				ExceptionService.throwCodeException("前置失败，无法识别该key格式1");
			}
    	}
	}
}
