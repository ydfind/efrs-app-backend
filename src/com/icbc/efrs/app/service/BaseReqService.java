package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.PCServerUrlsProp;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * app请求结果相关功能集合
 *
 */
public class BaseReqService {
	// 根据app请求报文，创建server请求
	public static String getPcResultStr(BaseAppReqEntity appReq){
		String result = null;
		// body解析出请求类型，校验格式
		BaseServerReqEntity serverReq = ServerReqService.getServerReqEntity(appReq);
		if(serverReq != null)
		    result = getPcResultStr(serverReq);
		return result;
	}
	
	public static String getPcResultStr(BaseServerReqEntity serverReq){
		String result = null;
		if(serverReq == null)
			return result;
		// body解析出请求类型，校验格式
		String url = serverReq.getPcReqUrl();
		String str = serverReq.getPCServerPost();
		result = PCPostService.callHttpService(url, str);
//		// DRD: 风险没有数据，先如此处理
//		if(ReqJsonFilesProp.getReqIntfEnums(serverReq.getServiceKey()) == ReqIntfEnums.FXWithParam ||
//				ReqJsonFilesProp.getReqIntfEnums(serverReq.getServiceKey()) == ReqIntfEnums.FXWithParams){
//			System.out.println("目前没有风险数据，先伪造部分数据");
//			String content = FileUtil.getContent("D:/风险3.json");
//			result = content;
//		}
		
		if(result == null){
			System.out.println("----获取接口失败：getPcResultStr");
		}
		else{
			result = BaseResultService.jsonParse(result);
//			JSONObject obj = JSON.parseObject(result, Feature.OrderedField);
//			result = obj.toString();
		}
		return result;
	}
	
	public static JSONObject getPcResultJson(BaseServerReqEntity serverReq){
		JSONObject obj = null;
		String result = getPcResultStr(serverReq);
		if(result != null){
			obj = JSON.parseObject(result, Feature.OrderedField);
		}
		return obj;
	}
	
	public static JSONObject getFXTransPCResultJson(BaseAppResultEntity appResult, BaseAppReqEntity appReq, String content){
		JSONObject result = null;
		String url = PCServerUrlsProp.getFengXianTransUrl();
		String str = ServerReqService.parseFXTransPost(appResult, appReq, content);
		if(str == null || str.equals("")){
			System.out.println("获取风险翻译失败0！");
		}else{
			result = PCPostService.getHttpServiceJson(url, str);
			if(result == null){
				System.out.println("----获取接口失败：getFXTransPCResultJson");
			}
		}
		return result;
	}
}
