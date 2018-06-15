package com.icbc.efrs.app.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.prop.JsonKeysProp;
/*
 * 处理翻译（key替换）、特殊字符处理等
 */
public class ProcessJsonService {
    /**
     * 把objSource中key满足keys的项，复制到objTarget
     */	
    public static void CopyByKeys(String[] keys, JSONObject objSource, JSONObject objTarget) {
		if(objSource.size() < 1)
			return;
		String key, newKey, value, newValue;
		for(int i = 0; i < keys.length; i++) {
			key = keys[i];
			if(objSource.containsKey(key)) {
				newKey = /*ProcessJsonService.*/ProcessJsonKey(key);// 翻译
				value = objSource.getString(key);
				newValue = trimJsonValue(value);
//				newValue.trim();
//				newValue = ProcessJsonValue(value);
				objTarget.put(newKey, newValue);	          // DRD: value这样存的只是指针，会不会存在问题？
			}
			else
			{
				// DRD: 是不是需要创建一个空节点？？？
			}
		}	
	}
    /**
     * 把sourceArr中每个JSONObject，将  key满足keys的部分，复制到targetArr
     */	
    public static void CopyByKeys(String[] keys, JSONArray objSourceArr, JSONArray objTargetArr) {
    	for(int i = 0; i < objSourceArr.size(); i++) {
    		JSONObject objSubJson = new JSONObject(true);
    		CopyByKeys(keys, objSourceArr.getJSONObject(i), objSubJson);
    		objTargetArr.add(objSubJson);
    	}
 	}	
	
//	public static void ProcessJson(JSONArray jsonArray) {
//	    // 对json数组进行翻译处理
//		for(int i = 0; i < jsonArray.size(); i++) {
//			JSONObject jsonObject = jsonArray.getJSONObject(i);
//			ProcessJson(jsonObject);
//		}
//		
//	}
    
    public static String trimJsonValue(String value) {
    	String resultStr;
		char[] startChars = {'{', '}', '(', ')', '?'};
		char[] endChars = {'{', '}', '(', ')', '?', ','};
    	char[] val = value.toCharArray();  
    	int len = val.length;
    	int st = 0;

    	boolean needContinue = true;
    	char ch;
    	while ((st < len) ) {
    	    needContinue = false;
			ch = val[st];
    		for(int j = 0; j < startChars.length; j++) {
				if(ch <= ' ' || startChars[j] == ch) {
		    	    st++;
		    	    needContinue = true;
		    	    break;
				}
			} 
    		System.out.print("<"+ch+">");
    		if(!needContinue)
    		    break;   			
    	}
    	System.out.println("");
    	while ((st < len)) {
    	    needContinue = false;
			ch = val[len - 1];
    		for(int j = 0; j < endChars.length; j++) {
				if(ch <= ' ' || endChars[j] == ch) {
					len--;
		    	    needContinue = true;
		    	    break;
				}
			} 
			System.out.print("<"+ch+">");
    		if(!needContinue)
      		  break;
    	}
    	System.out.println("");
    	resultStr = ((st > 0) || (len < val.length)) ? value.substring(st, len) : value;
    	System.out.println("最终结果为：" + resultStr);
    	return resultStr;
    }
	
//	public static String ProcessJsonValue(String value) {
//		String newValue = value;
//		char[] speChars = {' ', '{', '}', '(', ')', '?', ' '};
//		int left = -1;
//		int right = newValue.length();
//		System.out.println("开始查找了----------");
//		// 从前往后找到第一个合法字符
//		for(int i = 0; i < newValue.length(); i++) {
//			char ch = newValue.charAt(i);
//			for(int j = 0; j < speChars.length; j++) {
//				if(speChars[j] == ch) {
//					left++;
//					break;
//				}
//			}
//			if(left != i)
//				break;	
//		}
//		left++;// 跳过当前字符
//		// 从后往前找到第一个合法字符
//		for(int i = newValue.length() - 1; i >= 0; i--) {
//			char ch = newValue.charAt(i);
//			for(int j = 0; j < speChars.length; j++) {
//				if(speChars[j] == ch) {
//					right--;
//					break;
//				}
//			}
//			if(right != i)
//				break;	
//		}
//		if(right > left)
//		    newValue = value.substring(left, right);
//		else
//			newValue = "";
//
//	    return newValue;		
//	}
//	
	public static String ProcessJsonKey(String key) {
		String newKey;
		if(JsonKeysProp.JsonKeyMap.containsKey(key)) 
			newKey = JsonKeysProp.JsonKeyMap.get(key);
		else
			newKey = key;
	    return newKey;	
	}
	
//	public static void ProcessJson(JSONObject jsonObject) {
//		Set<String> Keys = jsonObject.keySet();
//		Set<String> tempkeys = new HashSet<String>();
//		for(String key: Keys) 
//			tempkeys.add(key);	
//
//		String newKey, oldValue, newValue;		
//		for(String key: tempkeys) {
//			oldValue = jsonObject.getString(key);
//			newKey = ProcessJsonService.ProcessJsonKey(key);
//			newValue = ProcessJsonService.ProcessJsonKey(oldValue);
//			if((newKey.compareTo(key) != 0) || (newValue.compareTo(oldValue) != 0)) {
//				if(!jsonObject.containsKey(newKey)) {
//					jsonObject.put(newKey, newValue);
//					jsonObject.remove(key);		
//				}
//				else{
//					// DRD:后面是翻译失败的错误处理
//				}	
//			}	
//		}		
//	}
}
