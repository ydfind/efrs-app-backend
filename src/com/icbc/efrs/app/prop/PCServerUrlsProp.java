package com.icbc.efrs.app.prop;

import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.service.ExceptionService;

public class PCServerUrlsProp {
	public static String getPCReqUrl(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai: 
	//		result = "http://122.26.13.145:16257/icbc/cocoa/json/com.icbc.efrs.dsf.service.FahaiService/1.0/getFahai";
			result = "http://122.64.45.18:16257/icbc/cocoa/json/com.icbc.efrs.dsf.service.FahaiService/1.0/getFahai";
			break;
		default:
			ExceptionService.throwCodeException("无法取得该接口类型对应的PC端url地址");
		}	
		return result;
	}
}
