package com.icbc.efrs.app.serverreq;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.domain.AppReqBaseEntity;

public class ServerReqBaseEntity {
	private String pcReqUrl;
	private Date reqTime;
	private JSONObject jsonObject;
	private AppReqBaseEntity appReq;
	public ServerReqBaseEntity(){
		
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
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
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
	public void setAppReq(AppReqBaseEntity appReq) {
		this.appReq = appReq;
	}
	public AppReqBaseEntity getAppReq() {
		return appReq;
	}
}
