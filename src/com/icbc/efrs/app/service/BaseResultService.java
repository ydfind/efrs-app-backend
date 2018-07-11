package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * APP返回结果处理
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
	
	public static void proccessAppResult(BaseAppResultEntity appResult, BaseAppReqEntity appReq, String serviceKey){
		if(appResult.isPcReqOK()){
			ReqIntfEnums type = ReqJsonFilesProp.getReqIntfEnums(serviceKey);
			// DRD: 若要分页，请在翻译前进行，避免过多翻译造成浪费
			// 目前仅风险需要分页、企业年报、中数带参数（2504，照面信息除外）
			ReqJsonFileEntity objFile = 
				ReqJsonFilesProp.getJsonFileEntity(serviceKey);
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
	    	ResultNullService.nullJsonKeys(appResult, serviceKey);
			// 翻译
			ResultTransService.transJsonKeys(appResult, appReq, type, serviceKey);
			// 前置
			ResultTopService.topJsonKeys(appResult, serviceKey);
			// 对Code进行处理
			try{
				ResultCodeService.processCodeNode(appResult);
			}catch(Exception e){
				ExceptionService.throwCodeException("----进行Code的处理报错了");
			}
		};
	}
	
	public static void proccessAppResult(BaseAppResultEntity appResult, BaseServerReqEntity serverReq,  BaseAppReqEntity appReq){
		if(appResult.isPcReqOK()){
			proccessAppResult(appResult, appReq, serverReq.getServiceKey());
		};
	}
	
	// 根据PC返回报文，创建 返回App结果对象，能处理成：code、msg、totalpage、totalnum
	public static BaseAppResultEntity getAppResultEntity(BaseServerReqEntity serverReq, BaseAppReqEntity appReq, String pcResult){
		JSONObject jsonSource = null;
		// 判断PC端返回的报文格式是否正确
		if((pcResult != null) && (!pcResult.equals(""))){
			pcResult = jsonParse(pcResult);
			jsonSource = (JSONObject)JSONUtil.strToJson(pcResult);
		}
		return getAppResultEntity(serverReq, appReq, jsonSource);
	}  
	
	public static ComplexAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, ArrayList<BaseServerReqEntity> serverReqs, 
			ArrayList<JSONObject> pcResults){
		ComplexAppResultEntity appResult = null;
		try{
			appResult = AppResultService.getAppResultEntity(appReq, pcResults);
		}catch(Exception e){
			ExceptionService.throwCodeException("无法构造ComplexAppResultEntity0：getAppResultEntity");
		}

		if(appResult != null){
			proccessAppResult(appResult, appReq, appReq.getServiceKey());
		}else{
			ExceptionService.throwCodeException("无法构造ComplexAppResultEntity：getAppResultEntity");
		}
		return appResult;
	}  

	private static BaseAppResultEntity getAppResultEntity(BaseServerReqEntity serverReq, BaseAppReqEntity appReq, JSONObject pcResult){	
		BaseAppResultEntity appResult = AppResultService.getAppResultEntity(appReq, serverReq.getServiceKey(), pcResult);
		proccessAppResult(appResult, serverReq, appReq);
		return appResult;
	} 
	public static ArrayList<BaseAppResultEntity> getAppResultsWithoutProcess(ArrayList<BaseServerReqEntity> serverReqs){
		if(serverReqs == null){
			ExceptionService.throwCodeException("无法请求对象集合为null的情况");
			return null;
		}
		ArrayList<BaseAppResultEntity> appResults = new ArrayList<BaseAppResultEntity>();
		ArrayList<JSONObject> pcReqResults = PCPostService.getPCResultJsons(serverReqs);
		// 
		for(int i = 0; i < pcReqResults.size(); i++){
			BaseAppResultEntity appResult = null;
			JSONObject pcReqResult = pcReqResults.get(i);
			if(pcReqResult != null){
				try{
					String serviceKey = serverReqs.get(i).getServiceKey();
					appResult = AppResultService.getAppResultEntity(serviceKey, pcReqResult);	
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
