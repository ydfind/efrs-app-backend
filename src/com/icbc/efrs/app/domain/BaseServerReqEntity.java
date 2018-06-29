package com.icbc.efrs.app.domain;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.prop.ServerProp;
import com.icbc.efrs.app.service.ExceptionService;
/*
 * App服务器端请求PC的类
 */
public class BaseServerReqEntity {
	private String pcReqUrl;
	private Date reqTime;
	private JSONObject jsonObject;
	private String serviceKey;// 前端和后端协定的请求servicekey
	private ReqIntfEnums reqIntf;   // 请求类型
	public BaseServerReqEntity(){
		
	}
	public void setPcReqUrl(String pcReqUrl) {
		this.pcReqUrl = pcReqUrl;
	}
	public String getPcReqUrl() {
		return pcReqUrl;
	}
	public void setReqTime(Date reqTime) {
		this.reqTime = reqTime;
	}
	public Date getReqTime() {
		return reqTime;
	}
	
	public String getReqTimeStr(){
		SimpleDateFormat df = new SimpleDateFormat(ServerProp.getGlobalDateFormatStr());
		return df.format(reqTime);
	}
	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}
	public JSONObject getJsonObject() {
		return jsonObject;
	}
	
	public String getPCServerPost(){
		String result = "[" + jsonObject.toString() + "]";
		return result;
	}
	
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
		reqIntf = ReqJsonFilesProp.getReqIntfEnums(serviceKey);
	}
	public String getServiceKey() {
		return serviceKey;
	}
	public void setReqIntf(ReqIntfEnums reqIntf) {
		this.reqIntf = reqIntf;
	}
	public ReqIntfEnums getReqIntf() {
		return reqIntf;
	}
}
