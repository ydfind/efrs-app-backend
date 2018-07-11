package com.icbc.efrs.app.domain;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.service.ExceptionService;

public class ComplexAppResultEntity extends BaseAppResultEntity {
	private ArrayList<BaseAppResultEntity> subResults;// pc返回的原jsonSource节点
	
	public ComplexAppResultEntity(){
		super();
	}

	public void setSubResults(ArrayList<BaseAppResultEntity> subResults) {
		this.subResults = subResults;
		if(subResults == null){
			this.subResults = new ArrayList<BaseAppResultEntity>();
		}
	}

	public ArrayList<BaseAppResultEntity> getSubResults() {
		return subResults;
	}
	
	public JSONObject getJsonSource() {
		ExceptionService.throwCodeException("复杂类型不应该访问到这里0");
		return null;
	}
	
	public void setJsonSData(Object jsonSData) {
		ExceptionService.throwCodeException("复杂类型不应该访问到这里1");
	}

	public Object getJsonSData() {
		ExceptionService.throwCodeException("复杂类型不应该访问到这里2");
		return null;
	}
	
	public void setJsonSource(JSONObject jsonObject) {
		ExceptionService.throwCodeException("复杂类型不应该访问到这里3");
	}

}
