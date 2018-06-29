package com.icbc.efrs.app.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.service.ExceptionService;
/*
 * App请求的结果类
 */
public class BaseAppResultEntity {
	/* 分页时默认从1开始   */
	public static final Integer DEF_START_PAGE = 1;
	public static final String NAME_DATA = new String("data");
	private JSONObject jsonSource;// pc返回的原jsonSource节点
	private JSONObject jsonTarget;// 将返回给App端的jsonSource节点
	private String code;// 后端返回的成功吗
	private String msg;// 报错信息
	private Integer totalpage;
	private Integer totalnum;
	private Object jsonSData;// json中data节点
	private Object jsonTData;
	private Integer listtype;
	private boolean pcReqOK;
	
	public BaseAppResultEntity() {
		listtype = 0;
		jsonSource = null;
	}
	
	public BaseAppResultEntity(JSONObject objJson) {
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

	public void setPcReqOK(boolean pcReqOK) {
		this.pcReqOK = pcReqOK;
	}

	public boolean isPcReqOK() {
		return pcReqOK;
	}
	
	public boolean haveContent(){
		boolean result = false;
		result = jsonTData != null && ((JSONArray)jsonTData).size() > 0;
		if(!(jsonTData instanceof JSONArray)){
			ExceptionService.throwCodeException("jsonData需要为JSONArray类型");
			// 首页模糊查询等data是jsonobject类型，但首页模糊查询不会调用该函数
		}
		if(!(result == (totalnum > 0))){
			ExceptionService.throwCodeException("jsonData的数据和totalnum判断不一致");
		}
		return result;
	}

	public void setJsonSData(Object jsonSData) {
		this.jsonSData = jsonSData;
	}

	public Object getJsonSData() {
		return jsonSData;
	}

	public void setJsonTData(Object jsonTData) {
		this.jsonTData = jsonTData;
	}

	public Object getJsonTData() {
		return jsonTData;
	}
	
}
