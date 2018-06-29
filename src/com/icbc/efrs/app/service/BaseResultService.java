package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.BaseAppReqEntity;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.domain.BaseServerReqEntity;
import com.icbc.efrs.app.domain.TransFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.prop.TransFilesProp;
import com.icbc.efrs.app.utils.JSONUtil;
import com.icbc.efrs.app.utils.ResultUtils;
/**
 * PC请求结果相关功能集合
 *
 */
public class BaseResultService {
	/**
	 * 去掉PC端返回结果中的反斜杠、空格等
	 * @param jsonStr
	 * @return
	 */
	// json中反斜杠处理
	public static String jsonParse(String jsonStr){
		if(jsonStr == null){
			return null;
		}
		// 去掉空格等
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher matd = p.matcher(jsonStr);
		jsonStr = matd.replaceAll("");
		// 去掉反斜杠
		try{
			jsonStr = (String) JSON.parse(jsonStr);
		}catch(Exception e){
			LoggerAspect.logInfo("log:结果不需要反系列化，报错可以忽略");
		}
		return jsonStr;
	}
	
	// 判断是否jsonobject格式
	public static boolean invalidateJsonObject(String jsonStr){
		boolean result = true;
		try{
			JSON.parseObject(jsonStr, Feature.OrderedField);
		}catch(Exception e){
			ExceptionService.throwCodeException("---PC服务器返回的结果无法识别为json格式");
			result = false;
		}
		return result;
	}
	
	// 根据PC返回报文，创建 返回App结果对象，能处理成：code、msg、totalpage、totalnum
	public static BaseAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, BaseServerReqEntity serverReq, String pcResult){
		ReqIntfEnums type = serverReq.getReqIntf();
		JSONObject jsonSource = null;
		
		// 判断PC端返回的报文格式是否正确
		if((pcResult != null) && (!pcResult.equals(""))){
			pcResult = jsonParse(pcResult);
			jsonSource = (JSONObject)JSONUtil.strToJson(pcResult);
		}
		BaseAppResultEntity appResult = AppResultService.getAppResultEntity(appReq, serverReq.getServiceKey(), jsonSource);
		if(appResult.isPcReqOK()){
			// DRD: 若要分页，请在翻译前进行，避免过多翻译造成浪费
			// 目前仅风险需要分页、企业年报、中数带参数（2504，照面信息除外）
			ReqJsonFileEntity objFile = 
				ReqJsonFilesProp.getJsonFileEntity(serverReq.getServiceKey());
			int pageid = objFile.getAsPaged();
			if(pageid != -1){
				if((pageid == 1) || type == ReqIntfEnums.FXWithParam || 
						type == ReqIntfEnums.FXWithParams || type == ReqIntfEnums.CompanyReport || 
						type == ReqIntfEnums.ZSWithParamNoPaged){
					int page = appReq.getPage();
					int size = appReq.getSize();
					ResultPageService.page(appResult, page, size);
				}
			}
			// 删除不要的字段
			ArrayList<String> keys = null;
	    	if(objFile != null)
	    		keys = objFile.getAsDelKeys();
	    	if((keys != null) && (keys.size() > 0)){// 仅存在需要前置的字段时，才进行前置
	    		ResultDelService.delJsonKeys(appResult, keys);
	    	}
	    	// null字段的处理：目前仅首页模糊查询使用
	    	ResultNullService.nullJsonKeys(appResult, serverReq.getServiceKey());
			// 翻译
			ResultTransService.transJsonKeys(appResult, appReq, serverReq.getReqIntf(), serverReq.getServiceKey());
			// 前置
			ResultTopService.topJsonKeys(appResult, serverReq.getServiceKey());
			// 对Code进行处理
			try{
				ResultCodeService.processCodeNode(appResult);
			}catch(Exception e){
				ExceptionService.throwCodeException("----进行Code的处理报错了");
			}
		};
		return appResult;
	}  
	
	public static BaseAppResultEntity getAppResultWithoutProcess(String serviceKey, JSONObject pcResult){
		BaseAppResultEntity appResult = null;
		if(pcResult == null || serviceKey == null || serviceKey.equals("")){
			ExceptionService.throwCodeException("无法对nul的jsonpc结果、serviceKey为空进行处理");
			return appResult;
		}
		appResult = AppResultService.getAppResultEntity(serviceKey, pcResult);
		return appResult;
	}

	public static ArrayList<BaseAppResultEntity> getAppResultsWithoutProcess(ArrayList<BaseServerReqEntity> serverReqs){
		if(serverReqs == null){
			ExceptionService.throwCodeException("无法请求对象集合为null的情况");
			return null;
		}
		ArrayList<BaseAppResultEntity> appResults = new ArrayList<BaseAppResultEntity>();
		// 得到请求结果
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> paramJsons = new ArrayList<String>();
		for(int i = 0; i < serverReqs.size(); i++){
			BaseServerReqEntity serverReq = serverReqs.get(i);
			// 构造请求的url、post参数
			if(serverReq != null){
				urls.add(serverReq.getPcReqUrl());
				paramJsons.add(serverReq.getPCServerPost());
			}else{
				urls.add(null);
				paramJsons.add(null);
			}
		}
		ArrayList<JSONObject> pcReqResults = PCPostService.getPCResultJsons(urls, paramJsons);
		// 
		for(int i = 0; i < pcReqResults.size(); i++){
			BaseAppResultEntity appResult = null;
			JSONObject pcReqResult = pcReqResults.get(i);
			if(pcReqResult != null){
				try{
					String serviceKey = serverReqs.get(i).getServiceKey();
					appResult = BaseResultService.getAppResultWithoutProcess(serviceKey, pcReqResult);	
				}catch(Exception e){
					ExceptionService.throwCodeException("格式转换失败！");
					appResult = null;
				}
			}
			appResults.add(appResult);
		}
		return appResults;
	}
}
