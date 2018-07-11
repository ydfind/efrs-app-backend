package com.icbc.efrs.app.domain;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.enums.ReqIntfEnums;

/*
 * App请求类
 */
public class BaseAppReqEntity {
	private int page;     // 请求的页码
	private int size;     // 每页的数量
	private String bankId;
	private String userId;
	private String key;   // 为空即不存在,通常为公司名称
	private ReqIntfEnums intfType;
	private String serviceKey;
	private JSONObject jsonObject;
	public BaseAppReqEntity() {
	}
	
	public void init(){
		
	}

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

	public void setIntfType(ReqIntfEnums intfType) {
		this.intfType = intfType;
	}

	public ReqIntfEnums getIntfType() {
		return intfType;
	}

	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	public String getServiceKey() {
		return serviceKey;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}
}
