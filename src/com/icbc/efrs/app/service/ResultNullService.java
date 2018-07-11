package com.icbc.efrs.app.service;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;

/**
 * 对PC返回结果，对于约定某些为空的字段，进行删除处理
 *
 */
public class ResultNullService {
	
	public static void nullJsonKeys(BaseAppResultEntity appResult, String serviceKey){
		ReqIntfEnums type = ReqJsonFilesProp.getReqIntfEnums(serviceKey);
	 	if(appResult.getJsonTData() == null || type == ReqIntfEnums.ErrIntf)
	 	{
	 		ExceptionService.throwCodeException("JsonTData为null，或type == ReqIntfEnums.ErrIntf");
    		return;
	 	}
    	//nullJsonKeys(appResult.getJsonTData(), type, serviceKey, 1);
	 	// 首页模糊查询下的行业，INDUSTRYPHY或INDUSTRYPHYNAME为空时，需要清掉该项
	 	if(type == ReqIntfEnums.FuzzyQuery){
	 		JSONObject jsonObject = (JSONObject)(appResult.getJsonTData());
	 		Object objValue;
	 		JSONArray obj;
	 		if(jsonObject.containsKey("INDUSTRYPHYCOUNT")){
	 			objValue = jsonObject.get("INDUSTRYPHYCOUNT");
	 			obj = (JSONArray)objValue;
		 		ArrayList<Integer> ids = new ArrayList<Integer>();
		 		for(int i = obj.size() - 1; i >= 0; i--){
		 			JSONObject objSub = obj.getJSONObject(i);
		 			if(checkFuzzyQuery(objSub)){
		 				ids.add(i);
		 			}
		 		}
			 	for(int i = 0; i < ids.size(); i++){
			 		int delid = ids.get(i);
			 		obj.remove(delid);
			 	}
	 		}
	 		else{
	 			ExceptionService.throwCodeException("不包含该节点或不是array类型");
	 		}
	 	}
	}
	 	
	private static boolean checkFuzzyQuery(JSONObject obj){
		boolean result = false;
		String key;
		Object value;
		
		key = "INDUSTRYPHYNAME";
		if(!obj.containsKey(key)){
			return true;
		}
		value = obj.get(key);
		if((value instanceof String) && ((String)value).equalsIgnoreCase("")){
			return true;
		}
		
		key = "INDUSTRYPHY";
		if(!obj.containsKey(key)){
			return true;
		}
		value = obj.get(key);
		if((value instanceof String) && ((String)value).equalsIgnoreCase("")){
			return true;
		}
		return result;
	}
		
}
