package com.icbc.efrs.app.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.service.ExceptionService;

public class JSONUtil {
    /**
     * string转json
     *
     * @param string 要转换的String
     * @return json
     */
    public static JSON strToJson(String string) {
        JSONObject jsonObj = null;
        try {
            jsonObj = JSON.parseObject(string, Feature.OrderedField);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * json转arrayList
     *
     * @param jsonStr 要转换的json
     * @return arrayList
     */
    public static ArrayList<String> jsonStrToAL(String jsonStr) {
        ArrayList list = null;
        try {
            list = JSONObject.parseObject(jsonStr, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json转ListHashMap
     *
     * @param jsonStr 要转换的json
     * @return ListHashMap
     */
    public static LinkedHashMap<String, JSONArray> jsonStrToLHMJsonArray(String jsonStr) {
        LinkedHashMap map = null;
        try {
            map = JSONObject.parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * json转ListHashMap
     *
     * @param jsonStr 要转换的json
     * @return ListHashMap
     */
    public static LinkedHashMap<String, String> jsonStrToLHM(String jsonStr) {
        LinkedHashMap map = null;
        try {
            map = JSONObject.parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String getStringByKey(JSONObject jsonObject, String key){
    	String result = "";
    	if(jsonObject.containsKey(key)){
    		result = jsonObject.getString(key);
    	}
    	return result;
    }
    
    public static int getIntegerByKey(JSONObject jsonObject, String key, Integer defValue){
    	int result = defValue;
    	if(jsonObject.containsKey(key)){
    		Object obj = jsonObject.get(key);
    		if(obj instanceof Integer){
    			result = ((Integer)obj).intValue();
    		}
    		else if(obj instanceof String){
    			if(((String)obj).equals(""))
    				result = 0;
    			else
    			    result = Integer.parseInt((String)obj);
    		}
    		else
    		{
    			ExceptionService.throwCodeException("不支持该类型：" + obj.toString());
    		}
    	}
    	return result;
    }

    public static void copyJSONObject(JSONObject source, JSONObject target){
    	Set<String> keys = source.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: keys) 
			tempkeys.add(key);			
		for(String key: tempkeys) {
			Object obj = source.get(key);
			if(obj instanceof JSONObject){
				String keyValue = ((JSONObject)obj).getString(key);
				target.put(key, keyValue);
			}
			else
				target.put(key, obj);// JSONArray对象直接加进入了
		}	
    }

    
}
