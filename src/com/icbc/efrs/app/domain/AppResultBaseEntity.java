package com.icbc.efrs.app.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.serverreq.ServerReqBaseEntity;

public class AppResultBaseEntity {
	private JSONObject jsonSource;
	private JSONObject jsonTarget;
	private String code;// 后端返回的成功吗
	private String msg;// 报错信息
	private Integer totalpage;
	private Integer totalnum;
	private Object jsonData;
	private Integer listtype;
	private ServerReqBaseEntity serverReq;
	
	public AppResultBaseEntity() {
		listtype = 0;
		jsonSource = null;
		serverReq = null;
	}
	
	public AppResultBaseEntity(JSONObject objJson) {
		this.jsonSource = objJson;
	}
	
	public JSONObject getJsonSource() {
		return jsonSource;
	}
	
	public void setJsonSource(JSONObject jsonObject) {
		this.jsonSource = jsonObject;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setTotalpage(Integer totalpage) {
		this.totalpage = totalpage;
	}

	public Integer getTotalpage() {
		return totalpage;
	}

	public void setTotalnum(Integer totalnum) {
		this.totalnum = totalnum;
	}

	public Integer getTotalnum() {
		return totalnum;
	}

	public void setJsonTarget(JSONObject jsonTarget) {
		this.jsonTarget = jsonTarget;
	}

	public JSONObject getJsonTarget() {
		return jsonTarget;
	}

	public void setJsonData(Object jsonData) {
		this.jsonData = jsonData;
	}

	public Object getJsonData() {
		return jsonData;
	}
	
	public String getAppResultStr(){
		String result = "";
		result = jsonTarget.toString();
		return result;
	}

	public void setListtype(Integer listtype) {
		this.listtype = listtype;
	}

	public Integer getListtype() {
		return listtype;
	}

	public void setServerReq(ServerReqBaseEntity serverReq) {
		this.serverReq = serverReq;
	}

	public ServerReqBaseEntity getServerReq() {
		return serverReq;
	}

}
