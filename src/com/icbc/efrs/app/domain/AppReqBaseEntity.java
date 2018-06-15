package com.icbc.efrs.app.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.utils.JSONUtil;

/*
 * App请求的json对应的实体
 */
public class AppReqBaseEntity {
	private int page;     // 请求的页码
	private int size;     // 每页的数量
	private ReqJsonFileEntity reqJsonFileEntity; // 根据"serviceKey"从配置文件类中找到
	private JSONObject jsonObject;
	public AppReqBaseEntity() {
		setReqJsonFileEntity(null);
	}
	
	public void init(){
		
	}

//	public AppReqBaseEntity(String JsonStr) {
//		JSONObject objJson = JSON.parseObject(JsonStr);
//		this.page = JSONUtil.getIntegerByKey(objJson, "page", 0);
//		this.size = JSONUtil.getIntegerByKey(objJson, "size", 0);
//		String serviceKey = JSONUtil.getStringByKey(objJson, "serviceKey");
//		setReqJsonFileEntity(ReqJsonFilesProp.getJsonFileEntity(serviceKey));	
//		
//		objJson.remove("serviceKey");
//	}
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setReqJsonFileEntity(ReqJsonFileEntity reqJsonFileEntity) {
		this.reqJsonFileEntity = reqJsonFileEntity;
	}

	public ReqJsonFileEntity getReqJsonFileEntity() {
		return reqJsonFileEntity;
	}
}
