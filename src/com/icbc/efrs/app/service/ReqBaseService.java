package com.icbc.efrs.app.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.AppReqBaseEntity;
import com.icbc.efrs.app.domain.FaHaiAppReqEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.PCServerUrlsProp;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.serverreq.*;
import com.icbc.efrs.app.utils.JSONUtil;

public class ReqBaseService {
	// 工厂函数：根据type创建AppReqBaseEntity
	protected static AppReqBaseEntity getAppReqEntity(ReqIntfEnums type){
		AppReqBaseEntity objAppReq = null;
		switch(type){
		case FaHai:
			objAppReq = new FaHaiAppReqEntity();
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型0");
		}
		return objAppReq;
	}
	// 工厂函数：识别前端的报文，创建AppReqBaseEntity，并进行初始化
	protected static AppReqBaseEntity getAppReqEntity(String body){
		AppReqBaseEntity objAppReq = null;

		JSONObject objJson = JSON.parseObject(body, Feature.OrderedField);
		int page = JSONUtil.getIntegerByKey(objJson, "page", 0);
		int size = JSONUtil.getIntegerByKey(objJson, "size", 0);
		String serviceKey = JSONUtil.getStringByKey(objJson, "serviceKey");
		ReqJsonFileEntity reqJsonFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);	
		
		objAppReq = getAppReqEntity(reqJsonFile.getReqType());
		objAppReq.setPage(page);
		objAppReq.setSize(size);
		objAppReq.setJsonObject(objJson);
		objAppReq.setReqJsonFileEntity(reqJsonFile);
		// 进行其它特殊的初始化
		objAppReq.init();
		// 该节点不需要
		objJson.remove("serviceKey");
		// DRD more:后续需要载入不需要加载的节点等
		return objAppReq;
	}
	// 根据app请求接口type，创建server请求
	protected static ServerReqBaseEntity getServerReqEntity(ReqIntfEnums type){
		ServerReqBaseEntity objServerReq = null;
		switch(type){
		case FaHai:
			objServerReq = new FaHaiServerReqEntity();
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
		}
		return objServerReq;
	}
	// 根据app请求obj，创建server请求obj
	protected static ServerReqBaseEntity getServerReqEntity(AppReqBaseEntity appReq){
		ServerReqBaseEntity serverReq = null;
		ReqIntfEnums reqType = appReq.getReqJsonFileEntity().getReqType();
		serverReq = getServerReqEntity(reqType);
		InitServerReqEntity(appReq, serverReq);
		return serverReq;
	}
	// 根据app请求报文，创建server请求
	public static ServerReqBaseEntity getServerReqEntity(String body){
		ServerReqBaseEntity objServerReq = null;
		AppReqBaseEntity objAppReq = getAppReqEntity(body);
		objServerReq = getServerReqEntity(objAppReq);
		return objServerReq;
	}

	protected static void InitServerReqEntity(AppReqBaseEntity appReq, ServerReqBaseEntity serverReq){
		// 将appreq转化为serverreq
		ReqIntfEnums reqType = appReq.getReqJsonFileEntity().getReqType();
		serverReq.setAppReq(appReq);
		serverReq.setPcReqUrl(PCServerUrlsProp.getPCReqUrl(reqType));
		serverReq.setReqTime(new Date());
		JSONObject objJson = new JSONObject(true);
		JSONUtil.copyJSONObject(appReq.getJsonObject(), objJson);
		JSONUtil.copyJSONObject(appReq.getReqJsonFileEntity().getJsonObject(), objJson);
		serverReq.setJsonObject(objJson);
		InitServerReqEntity(appReq.getReqJsonFileEntity().getReqType(), appReq, serverReq);
	}
	
	protected static void InitServerReqEntity(ReqIntfEnums type, AppReqBaseEntity appReq, ServerReqBaseEntity serverReq){
		switch(type){
		case FaHai:
			serverReq.setReqTime(new Date());
			JSONObject obj = serverReq.getJsonObject();
			obj.put("time", serverReq.getReqTimeStr());
//			String tableType = appReq.getReqJsonFileEntity().getReqParams().get(0);// 第一个参数为查询的类型
//			obj.put("tableType", tableType);  
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型2");
		}
	}
}
