package com.icbc.efrs.app.utils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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

    /**
     * 复制source中所有节点到target
     * 
     */
    public static void copyJSONObject(JSONObject source, JSONObject target){
    	Set<String> keys = source.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: keys){ 
			tempkeys.add(key);
		}
		for(String key: tempkeys) {
			Object obj = source.get(key);
			if(obj instanceof String){
				String keyValue = (String)obj;
				target.put(key, keyValue);
			}
			else{
				target.put(key, obj);// JSONArray对象直接加进入了
			};
		}	
    }
    /**
     * 复制source中所有节点到target
     * 
     */
    public static void copyJSONArray(JSONArray source, JSONArray target){
    	for(int i = 0; i < source.size(); i++){
    		target.add(source.get(i));
    	}
    }
    /**
     * 复制source中某节点到target
     * 
     * @param source JSONObject源
     * @param target JSONObject目标
     * @param key 需要查找的节点的key
     * @return 是否复制成功
     */
    public static boolean copyJSONObjectField(JSONObject source, JSONObject target, String key){
    	boolean result = source.containsKey(key);
    	if(result){
    		target.put(key, source.get(key));
    	}
    	return result;
    }
    /**
     * 查找并返回子节点所在的JSONObject对象
     * 
     * @param jsonObject 查找的父节点
     * @param key 需要查找的节点的key
     * @return key节点对应的父JSONObject，若key对应节点不存在，返回null
     */
    public static JSONObject searchKeyParent(JSONObject jsonObject, String key){
		String[] keys = key.split("\\.");
		return searchKeyParent(jsonObject, keys);	
    }
    
    public static JSONObject searchKeyParent(JSONObject jsonObject, String[] keys){
    	JSONObject node = jsonObject;
		if(keys.length > 0){
	    	try{
	    		for(int i = 0; i < keys.length - 1; i++){
	    			node = node.getJSONObject(keys[i]);
	    		}
	    		if(!node.containsKey(keys[keys.length - 1])){
	    			node = null;
	    		}
	    	}catch(Exception e){
	    		ExceptionService.throwCodeException("Can not find the key ");
	    		node = null;
	    	}
		}
    	return node;
    	
    }
    
    /**
     * 查找并返回子节点的值
     * 
     * @param jsonObject 查找的父节点
     * @param key 需要查找的节点的key
     * @return key节点对应value的Object类型值
     */
    public static Object getSubNodeObj(JSONObject jsonObject, String key){
    	Object ret = null;
    	JSONObject node = jsonObject;
		String[] keys = key.split("\\.");
		if(keys.length > 0){
	    	try{
	    		for(int i = 0; i < keys.length - 1; i++){
	    			node = node.getJSONObject(keys[i]);
	    		}
	    		ret = node.get(keys[keys.length - 1]);
	    	}catch(Exception e){
	    		ExceptionService.throwCodeException("Can not find the key " + key);
	    		ret = null;
	    	}
		}
    	return ret;
    }

    /**
     * 查找并返回子节点的值
     * 
     * @param jsonObject 查找的父节点
     * @param key 需要查找的节点的key
     * @return key节点对应value的int类型值
     */
    public static int getSubNodeInt(JSONObject jsonObject, String key, int defValue){
    	Object obj = getSubNodeObj(jsonObject, key);
    	if(obj == null){
    		return defValue;
    	}
    	else if(obj instanceof String){
    		return Integer.parseInt((String)obj);
    	}
    	else if(obj instanceof Integer){
    		return (Integer)obj;
    	}
    	else{
    		ExceptionService.throwCodeException("the value of the " + key + " is not int ");
    	}
    	return defValue;
    }

    /**
     * 查找并返回子节点的值
     * 
     * @param jsonObject 查找的父节点
     * @param key 需要查找的节点的key
     * @return key节点对应value的string类型值
     */
    public static String getSubNodeStr(JSONObject jsonObject, String key){
    	Object obj = getSubNodeObj(jsonObject, key);
    	if(obj == null){
//    		LoggerAspect.logInfo("---------the value of the " + key + " is not str ");
    		return null;
    	}
    	else if(obj instanceof String){
    		return (String)obj;
    	}
    	else if(obj instanceof Integer){
    		return String.valueOf((Integer)obj);
    	}
    	else{
    		ExceptionService.throwCodeException("the value of the " + key + " is not str ");
    	}
    	return null;
    }
    public static String getStringByKey(JSONObject jsonObject, String key){
    	return getSubNodeStr(jsonObject, key);
    }
    
    public static int getIntegerByKey(JSONObject jsonObject, String key, int defValue){
    	return getSubNodeInt(jsonObject, key, defValue);
    }

    
    /**
     * 对JSONObject进行分页处理，默认从0页开始
     * 
     * @param page 请求的当前页
     * @param size 每页的记录数
     * @return 是否分页成功
     */
    public static boolean pagingJson(JSONObject jsonObject, int page, int size){
    	if((size < 1) || (page < 0)){
    		return false;
    	}
    	int start = page * size;
    	int end = (page + 1) * size;
    	boolean result = true;
    	
    	Map<String, Object> objMap = jsonObject.getInnerMap();
    	if(objMap instanceof LinkedHashMap){
    		// 取得分页内数据
			LinkedHashMap objLinked = (LinkedHashMap)objMap;
			ArrayList<Map.Entry<String, Object>> targetList = 
				new ArrayList<Map.Entry<String, Object>>();
			ArrayList<Map.Entry<String, Object>> list = 
				new ArrayList<Map.Entry<String, Object>>(objLinked.entrySet());
			for(int i = start; (i < end) && (i < list.size()); i++){
				Object obj = list.get(i).getValue();
				String key = list.get(i).getKey();
				targetList.add(new AbstractMap.SimpleEntry<String, Object>(key, obj));
			}
			// 建立分页
			jsonObject.clear();
			for(int i = 0; i < targetList.size(); i++){
				Object obj = targetList.get(i).getValue();
				String key = targetList.get(i).getKey();
				jsonObject.put(key, obj);
			}
    	}
    	else{
    		result = false;
    	}
    	
    	return result;
    }
    
    public static boolean pagingJson(JSONObject jsonObject, int page, int size, int startPage){
    	return pagingJson(jsonObject, page - startPage, size);
    }
    /**
     * 对JSONArray进行分页处理，默认从0页开始
     * 
     * @param page 请求的当前页
     * @param size 每页的记录数
     * @return 是否分页成功
     */
    private static boolean pagingJson(JSONArray jsonArray, int page, int size){
    	if((size < 1) || (page < 0))
    	{
    		ExceptionService.throwCodeException("传入分页参数有误，无法分页！");
    		return false;
    	}
    	int start = page * size;
    	int end = (page + 1) * size;
    	boolean result = true;
    	
    	for(int i = jsonArray.size() - 1; i >= end ; i--){
    		jsonArray.remove(i);
    	}
    	start = Math.min(start, jsonArray.size());
    	for(int i = start - 1; i >= 0; i--){
    		jsonArray.remove(i);
    	}
    	return result;
    }
  
    /**
     * 对JSONArray进行分页处理
     * 
     * @param page 请求的当前页
     * @param size 每页的记录数
     * @param startPage 从第几页开始
     * @return 是否分页成功
     */
    public static boolean pagingJson(JSONArray jsonArray, int page, int size, int startPage){
    	return pagingJson(jsonArray, page - startPage, size);
    }
}
