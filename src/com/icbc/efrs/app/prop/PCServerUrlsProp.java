package com.icbc.efrs.app.prop;

import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.service.ExceptionService;
/**
 * 向PC请求的url配置
 *
 */
public class PCServerUrlsProp {
	private static String urlIP = "122.26.13.145:16257";// 7月
//	private static String urlIP = "122.64.45.18:16257";// 6月
	public static String getPCReqUrl(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai: 
			result = "http://" + urlIP + 
			  "/icbc/cocoa/json/com.icbc.efrs.dsf.service.FahaiService/1.0/getFahai";
			break;
		case FuzzyQuery:
			result = "http://" + urlIP + 
			  "/icbc/cocoa/json/com.icbc.efrs.dsf.chinadaas.CdDataService/1.1/getCdDataService";
			break;
		case FXWithParam:
		case FXWithParams:
			result = "http://" + urlIP + 
			  "/icbc/cocoa/json/com.icbc.efrs.dsf.compositeservice.CompleteService/1.0/getCompleteService";
			break;	
		// 中数分页及不分页接口url都是一样的
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case ZSWithParam:
		case PatentInfo:
			result = "http://" + urlIP + 
			  "/icbc/cocoa/json/com.icbc.efrs.dsf.chinadaas.CdDataService/1.1/getCdDataService";
			break;
//		case AbnormalManage:
//		case QualityCertification:
//      case TeleFraud:
//		case TaxIllegal:
		default:
			ExceptionService.throwCodeException("无法取得该接口类型对应的PC端url地址");
		}	
		return result;
	}
	
	public static String getFengXianTransUrl(){
		String result;
		result = "http://" + urlIP + 
		  "/icbc/cocoa/json/com.icbc.efrs.dsf.compositeservice.MajorService/1.0/getMajorService";
		return result;
	}
}
