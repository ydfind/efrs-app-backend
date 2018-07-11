package com.icbc.efrs.app.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.BaseAppReqEntity;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.ComplexAppResultEntity;
import com.icbc.efrs.app.domain.TransFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.TransFilesProp;

/**
 * 对PC返回结果，进行翻译
 * @author kfzx-dengrd
 *
 */
public class ResultTransService {
	
    public static void transJsonKeys(BaseAppResultEntity appResult, BaseAppReqEntity appReq, ReqIntfEnums type, String serviceKey){
    	if(appResult.getJsonTData() == null || type == ReqIntfEnums.ErrIntf || 
    			serviceKey == null || serviceKey.equals(""))
    		return;
    	Map<String, String> fileMap = getTransMap(appResult, appReq, type, serviceKey);
    	
	    if(fileMap != null){
	    	transJsonKeys(appResult.getJsonTData(), fileMap);
	    }
    }
    
    private static Map<String, String> getTransMap(BaseAppResultEntity appResult, BaseAppReqEntity appReq, ReqIntfEnums type, String serviceKey){
    	Map<String, String> result = null;
    	switch(type){
    	case FaHai:
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case PatentInfo:
		case ZSWithParam:
			if(TransFilesProp.getFileMap().containsKey(serviceKey)){
	    	    TransFileEntity objFile = TransFilesProp.getTransFileEntity(serviceKey);
		    	if(objFile != null)
		    		result = objFile.getKeyMap();
	    	}else{
	    		LoggerAspect.logWarn("无法找到翻译文件，将不进行翻译！");
	    	}
			break;
		case FXWithParam:
		case FXWithParams:
			TransFileEntity objFile = TransFilesProp.getFXTransFileEntity(appResult, appReq);
	    	if(objFile != null)
	    		result = objFile.getKeyMap();
			break;
		case AbnormalManage:
		case QualityCertification:
		case TeleFraud:
		case TaxIllegal:
			if(appResult instanceof ComplexAppResultEntity){
	    		result = TransFilesProp.getComplexTransMap((ComplexAppResultEntity)appResult, appReq);
	    	}else{
	    		ExceptionService.throwCodeException("报错：经营异常结果非复杂类型");
	    	}
			break;
		default: 
			ExceptionService.throwCodeException("无法识别此接口类型的翻译map");
    	}
    	return result;
    }
    
    private static void transJsonKeys(Object obj, Map<String, String> fileMap){
		if(obj instanceof JSONObject){
			transJsonKeys((JSONObject)obj, fileMap);
		}else if(obj instanceof JSONArray){
			transJsonKeys((JSONArray)obj, fileMap);
		}else{
			ExceptionService.throwCodeException("翻译失败，无法识别该key格式0");
		}
    }
    
    private static void transJsonKeys(JSONObject obj, Map<String, String> fileMap){
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
				transJsonKeys((JSONObject)objValue, fileMap);
			}else if(objValue instanceof JSONArray){
				transJsonKeys((JSONArray)objValue, fileMap);
			}
			// 通常只有叶子节点需要，但企业年报中叶子节点的父节点也要翻译
			if(fileMap.containsKey(key)){
				newKey = fileMap.get(key);
			}
			if(newKey.compareTo(key) != 0) {
				if(!obj.containsKey(newKey)) {
					obj.put(newKey, objValue);
					obj.remove(key);		
				}
				else{
					ExceptionService.throwCodeException("翻译失败" + key + " to " + newKey);
				}	
			}	
		}	
    	
    }
    
    private static void transJsonKeys(JSONArray obj, Map<String, String> fileMap){	
		Object objValue;
    	for(int i = obj.size() - 1; i >= 0; i--){
			objValue = obj.get(i); 
    		if(objValue instanceof JSONObject){
    			transJsonKeys((JSONObject)objValue, fileMap);
			}else if(objValue instanceof JSONArray){
				transJsonKeys((JSONArray)objValue, fileMap);
			}else{
				ExceptionService.throwCodeException("翻译失败，无法识别该key格式1");
			}
    	}
    }
    
    private static String processJsonKey(String key, Map<String, String> fileMap) {
		String newKey;
		if(fileMap.containsKey(key)) 
			newKey = fileMap.get(key);
		else
			newKey = key;
	    return newKey;	
	}

}
