package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.AppResultBaseEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.domain.TransFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.prop.JsonKeysProp;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.prop.TransFilesProp;
import com.icbc.efrs.app.serverreq.FaHaiServerReqEntity;
import com.icbc.efrs.app.serverreq.ServerReqBaseEntity;
import com.icbc.efrs.app.utils.JSONUtil;
import com.icbc.efrs.app.utils.ResultUtils;

public class JsonFormatBaseService {
	// json中反斜杠处理
	public static String jsonParse(String jsonStr){
		try{
			jsonStr = (String) JSON.parse(jsonStr);
		}catch(Exception e){
			
		}
		return jsonStr;
	}
	// 判断是否jsonobject格式
	public static boolean invalidateJsonObject(String jsonStr){
		boolean result = true;
		try{
			JSONObject objJson = JSON.parseObject(jsonStr, Feature.OrderedField);
		}catch(Exception e){
			ExceptionService.throwCodeException("---PC服务器返回的结果无法识别为json格式");
			result = false;
		}
		return result;
	}
	// 工厂：根据type创建，app请求返回对象
	public static AppResultBaseEntity getAppResultEntity(ReqIntfEnums type){
		AppResultBaseEntity objAppRet = null;
		switch(type){
		case FaHai:
			objAppRet = new AppResultBaseEntity();
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
		}
		return objAppRet;
	}
	// 返回code的key
	public static String getCodeKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "respCd";
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的成功码");
		}
		return result;
	}
	// 返回msg的key
	public static String getMsgKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "msg";
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的msg");
		}
		return result;
	}
	// 返回data的key
	public static String getDataKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "result";
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的msg");
		}
		return result;
	}
	// 返回totalnum的key
	public static String getTotalNumKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "totalNum";
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的总数量key");
		}
		return result;
	}
	// 识别总数、data节点、成功码、msg
	public static void initAppResultBaseField(AppResultBaseEntity appResult, ServerReqBaseEntity serverReq){
		ReqIntfEnums type = serverReq.getAppReq().getReqJsonFileEntity().getReqType(); 
		JSONObject jsonSource = appResult.getJsonSource();
//		JSONObject jsonTarget = appResult.getJsonTarget();
		String key;
		// msg
		key = getMsgKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			appResult.setMsg(jsonSource.getString(key));
		}else{
			appResult.setMsg("PC服务器错误：找不到后端返回的msg节点");
		}
		// code节点
		key = getCodeKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			appResult.setCode(jsonSource.getString(key));
		}else{
			appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
			appResult.setMsg("PC服务器错误：找不到后端返回的成功码节点");
		}
		// totalNum
		key = getTotalNumKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			int totalnum = JSONUtil.getIntegerByKey(jsonSource, key, 0);
			appResult.setTotalnum(totalnum);
		}else{
			appResult.setTotalnum(0);
		}
		// Data节点识别
		key = getDataKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			appResult.setJsonData(jsonSource.get(key));
		}else{
			//appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
			appResult.setJsonData(null);
			ExceptionService.throwCodeException("无法从后台服务器返回报文中找到data节点");
		}
	}
	// 进行json的组装
	public static void processAppResult(AppResultBaseEntity appResult, ServerReqBaseEntity serverReq){
		ReqIntfEnums type = serverReq.getAppReq().getReqJsonFileEntity().getReqType(); 
//		int listtype = 0;//1----企业年报查询;2----动产抵押查询;0----其他信息查询
		// 特殊业务
		switch(type){
		case FaHai:
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
		}
	}
	// 产生app请求返回对象中的json报文
	public static void buildAppResultJson(AppResultBaseEntity appResult){
		Double pagesize = 10.0;// 以使计算结果能四舍五入
		int totalpage = 0;
		// 通常业务
		JSONObject jsonTarget = appResult.getJsonTarget();
		jsonTarget.put("msg", appResult.getMsg());
		jsonTarget.put("code", appResult.getCode());
		jsonTarget.put("totalnum", String.valueOf(appResult.getTotalnum()));
		totalpage = (int)Math.ceil(appResult.getTotalnum() / pagesize);
		jsonTarget.put("totalpage", String.valueOf(totalpage));
		jsonTarget.put("listtype", String.valueOf(appResult.getListtype()));
		if(appResult.getJsonData() == null){
			appResult.setJsonData(new JSONObject(true));
		}
		jsonTarget.put("data", appResult.getJsonData());
	}
	// 根据PC返回报文，创建 返回App结果对象，能处理成：code、msg、totalpage、totalnum
	public static AppResultBaseEntity getAppResultEntity(ServerReqBaseEntity serverReq, String pcResult){
		ReqIntfEnums type = serverReq.getAppReq().getReqJsonFileEntity().getReqType(); 
		AppResultBaseEntity appResult = getAppResultEntity(type);
		appResult.setServerReq(serverReq);
		JSONObject jsonSource = null;
		JSONObject jsonTarget = null;
		// 判断PC端返回的报文格式是否正确
		boolean reqSuccess = true;
		if((pcResult == null) || (pcResult.equals("")))
			reqSuccess = false;
		else{
			pcResult = jsonParse(pcResult);
			jsonSource = (JSONObject)JSONUtil.strToJson(pcResult);
			reqSuccess = !(jsonSource == null);
		}
		if(reqSuccess){
			appResult.setJsonSource(jsonSource);
			jsonTarget = new JSONObject(true);
			appResult.setJsonTarget(jsonTarget);
			initAppResultBaseField(appResult, serverReq);
			processAppResult(appResult, serverReq);
			buildAppResultJson(appResult);
			// DRD: 若要分页，请在翻译前进行，避免过多翻译造成浪费
			delJsonKeys(appResult);
			processTrans(appResult);
			// 前置
			topJsonKeys(appResult);
		}
		else{
			// 先简单设置吧
			appResult.setCode("-1");
			appResult.setMsg("PC服务器返回为空");
			appResult.setTotalnum(0);
			appResult.setTotalpage(0);
			buildAppResultJson(appResult);
		}
		
//		appResult.setJsonObject(jsonObject);
//		
		
//		if(result == null){
//			System.out.println("获取接口失败");
//			result = ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();			
////			String ret = FileUtil.getContent("D:/文档/法海/裁判文书-报文.json");
////			result = ret;
//		}	
		return appResult;
	}
    // 处理翻译
    public static void processTrans(AppResultBaseEntity appResult){
    	if(appResult.getJsonData() == null)
    		return;
    	Map<String, String> fileMap = null;
    	TransFileEntity objFile = TransFilesProp.getTransFileEntity(
    			appResult.getServerReq().getAppReq().getReqJsonFileEntity().getReqKey());
    	if(objFile != null)
    		fileMap = objFile.getKeyMap();
    	if(fileMap != null){
    		processTrans(appResult.getJsonData(), fileMap);
    	}
    }
    
    public static void processTrans(Object obj, Map<String, String> fileMap){
		if(obj instanceof JSONObject){
			processTrans((JSONObject)obj, fileMap);
		}else if(obj instanceof JSONArray){
			processTrans((JSONArray)obj, fileMap);
		}else{
			ExceptionService.throwCodeException("翻译失败，无法识别该key格式0");
			//
		}
    }
    
 //   public static String getTrans(String key, )
    
    public static void processTrans(JSONObject obj, Map<String, String> fileMap){
		Set<String> Keys = obj.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: Keys) 
			tempkeys.add(key);	

		String newKey;	
		Object objValue;
		for(String key: tempkeys) {
			newKey = key;
			if(key.equalsIgnoreCase("COURTROOM")){
				System.out.println("------------------------" + key);
			}
			objValue = obj.get(key);
			if(objValue instanceof JSONObject){
				processTrans((JSONObject)objValue, fileMap);
			}else if(objValue instanceof JSONArray){
				processTrans((JSONArray)objValue, fileMap);
			}else if(fileMap.containsKey(key)){
				newKey = fileMap.get(key);
			}
//			newValue = ProcessJsonService.ProcessJsonKey(oldValue);
			if(newKey.compareTo(key) != 0) {
				if(!obj.containsKey(newKey)) {
					obj.put(newKey, objValue);
					obj.remove(key);		
				}
				else{
					ExceptionService.throwCodeException("翻译失败" + key + " to " + newKey);
					// DRD:后面是翻译失败的错误处理
				}	
			}	
		}	
    	
    }
    
    public static void processTrans(JSONArray obj, Map<String, String> fileMap){	
		Object objValue;
    	for(int i = obj.size() - 1; i >= 0; i--){
			objValue = obj.get(i); 
    		if(objValue instanceof JSONObject){
				processTrans((JSONObject)objValue, fileMap);
			}else if(objValue instanceof JSONArray){
				processTrans((JSONArray)objValue, fileMap);
			}else{
				ExceptionService.throwCodeException("翻译失败，无法识别该key格式1");
			}
    	}
    }
    
	public static String processJsonKey(String key, Map<String, String> fileMap) {
		String newKey;
		if(fileMap.containsKey(key)) 
			newKey = fileMap.get(key);
		else
			newKey = key;
	    return newKey;	
	}
	// 字段前置处理
	public static void topJsonKeys(AppResultBaseEntity appResult){
	 	if(appResult.getJsonData() == null)
    		return;
	 	ArrayList<String> keys = null;
    	ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(
    			appResult.getServerReq().getAppReq().getReqJsonFileEntity().getReqKey());
    	if(objFile != null)
    		keys = objFile.getAsTopKeys();
    	if((keys != null) && (keys.size() > 0)){// 仅存在需要前置的字段时，才进行前置
    		topJsonKeys(appResult.getJsonData(), keys);
    	}
	}
	
	public static void topJsonKeys(Object obj, ArrayList<String> keys){
		if(obj instanceof JSONObject){
			topJsonKeys((JSONObject)obj, keys);
		}else if(obj instanceof JSONArray){
			topJsonKeys((JSONArray)obj, keys);
		}else{
			ExceptionService.throwCodeException("前置失败，无法识别该key格式0");
		}
	}
	
	public static void topJsonKeys(JSONObject obj, ArrayList<String> keys){
		Set<String> Keys = obj.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: Keys) 
			tempkeys.add(key);	

		String newKey;	
		Object objValue;
		for(String key: tempkeys) {
			newKey = key;
			if(key.equalsIgnoreCase("COURTROOM")){
				System.out.println("------------------------" + key);
			}
			objValue = obj.get(key);
			if(objValue instanceof JSONObject){
				topJsonKeys((JSONObject)objValue, keys);
			}else if(objValue instanceof JSONArray){
				topJsonKeys((JSONArray)objValue, keys);
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
				for(Map.Entry<String, Object> entry: list){
					objLinked.remove(entry.getKey());
					objLinked.put(entry.getKey(), entry.getValue());
				}
				
			}
		}
	}
	
	public static void topJsonKeys(JSONArray obj, ArrayList<String> keys){
		Object objValue;
    	for(int i = obj.size() - 1; i >= 0; i--){
			objValue = obj.get(i); 
    		if(objValue instanceof JSONObject){
    			topJsonKeys((JSONObject)objValue, keys);
			}else if(objValue instanceof JSONArray){
				topJsonKeys((JSONArray)objValue, keys);
			}else{
				ExceptionService.throwCodeException("前置失败，无法识别该key格式1");
			}
    	}
	}
	// 需要删除的字段处理
	public static void delJsonKeys(AppResultBaseEntity appResult){
	 	if(appResult.getJsonData() == null)
    		return;
	 	ArrayList<String> keys = null;
    	ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(
    			appResult.getServerReq().getAppReq().getReqJsonFileEntity().getReqKey());
    	if(objFile != null)
    		keys = objFile.getAsDelKeys();
    	if((keys != null) && (keys.size() > 0)){// 仅存在需要前置的字段时，才进行前置
    		delJsonKeys(appResult.getJsonData(), keys);
    	}
	}
	
	public static void delJsonKeys(Object obj, ArrayList<String> keys){
		if(obj instanceof JSONObject){
			delJsonKeys((JSONObject)obj, keys);
		}else if(obj instanceof JSONArray){
			delJsonKeys((JSONArray)obj, keys);
		}else{
			ExceptionService.throwCodeException("删除key失败，无法识别该key格式0");
		}
	}
	
	public static void delJsonKeys(JSONObject obj, ArrayList<String> keys){
		Set<String> Keys = obj.keySet();
		Set<String> tempkeys = new HashSet<String>();
		for(String key: Keys) 
			tempkeys.add(key);	

		String newKey;	
		Object objValue;
		for(String key: tempkeys) {
			newKey = key;
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
	
	public static void delJsonKeys(JSONArray obj, ArrayList<String> keys){
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
