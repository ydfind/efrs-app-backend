package com.icbc.efrs.app.domain;

import com.alibaba.fastjson.JSONObject;

public class BaseJsonDisplayEntity {
	protected JSONObject jsonObject;
	
	public BaseJsonDisplayEntity() {
		jsonObject = new JSONObject(true);
	}
	
//	public BaseJsonDisplayEntity(JSONObject objJson) {
//		this.jsonObject = objJson;
//	}
	
	public JSONObject getJsonObject() {
		return jsonObject;
	}
	
//	public void setJsonObject(JSONObject jsonObject) {
//		this.jsonObject = jsonObject;
//	}

}
