package com.icbc.efrs.app.service;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.domain.BaseAppReqEntity;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.BaseServerReqEntity;
import com.icbc.efrs.app.domain.ComplexAppResultEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;

/**
 * 负责直接对接Controller的业务
 */
public class AppService {
	/**
	 * 处理通常的App请求：法海、模糊查询、风险等
	 * @param appReq 请求对象
	 * @return 返回请求结果string
	 */
	public static String getAppResultStr(BaseAppReqEntity appReq){
		String result = null;

		BaseServerReqEntity serverReq = ServerReqService.getServerReqEntity(appReq);
		if(serverReq != null)
		    result = PCPostService.getPcResultStr(serverReq);	
		try{
			BaseAppResultEntity appResult = BaseResultService.getAppResultEntity(serverReq, appReq, result);
			result = appResult.getAppResultStr();	
		}catch(Exception e){
			ExceptionService.throwCodeException("格式转换失败！");
		}
		return result;
	}
	
	/**
	 * 处理企业查询
	 * @param appReq 请求对象
	 * @return 企业查询的结果string
	 */
	public static String getCompanyQueryStr(BaseAppReqEntity appReq){
		String result = "";
		// 构建x个请求的key: params
		ArrayList<String> params = ReqJsonFilesProp.getJsonFileEntity(appReq.getServiceKey()).getAsParams();
		// 构建请求报文: serverReqs
		ArrayList<BaseServerReqEntity> serverReqs = ServerReqService.getServerReqs(params, appReq); 
		// 构建PC请求结果集合(BaseAppResultEntity类型): appResults
		ArrayList<BaseAppResultEntity> appResults = BaseResultService.getAppResultsWithoutProcess(serverReqs); 
		// 构建与前端约定的json格式
		JSONArray data = new JSONArray();
		if(appResults == null || appResults.size() != params.size()){
			ExceptionService.throwCodeException("企业查询结果为null，不期待的结果");
			for(int i = 0; i < params.size(); i++){
				data.add("0");
			}
		}else{
			for(int i = 0; i < appResults.size(); i++){
				String resultItem = "0";
				BaseAppResultEntity appResult = appResults.get(i);
				if(appResult != null && appResult.haveContent()){
					resultItem = "1";
				}
				data.add(resultItem);
			}
		}
		JSONObject root = new JSONObject(true);
	    root.put(Constants.RES_NAME_DATA, data);
	    result = root.toJSONString();
		return result;
	}
	
	/**
	 * 处理经营异常
	 * @param appReq 请求对象
	 * @return 请求的结果string
	 */
	public static String getComplexResultStr(BaseAppReqEntity appReq){
		ComplexAppResultEntity appResult = getComplexAppResult(appReq);
		if(appResult == null){
			ExceptionService.throwCodeException("复杂请求，处理失败！");
			return "";
		}
	    String result = appResult.getAppResultStr();
	    LoggerAspect.logInfo("复杂请求结果为：" + result);
		return result;
	}
	
	/**
	 * 根据请求创建ComplexAppResultEntity结果对象
	 * @param appReq
	 * @return
	 */
//	private static ComplexAppResultEntity createComplexAppResult(BaseAppReqEntity appReq){
//		// 校验检查
//		if(appReq == null){
//			ExceptionService.throwCodeException("请求参数错误！" + appReq.getServiceKey());
//			return null;
//		}
//		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(appReq.getServiceKey());
//		if(objFile == null){
//			ExceptionService.throwCodeException("不支持该接口，未找到对应的配置文件" + appReq.getServiceKey());
//			return null;
//		}
//		ComplexAppResultEntity objResult = null;
//		// 创建
//		BaseAppResultEntity objBase = AppResultService.getAppResultEntity(objFile.getReqType());
//		if(objBase instanceof ComplexAppResultEntity){
//			objResult = (ComplexAppResultEntity)objBase;
//		}else{
//			ExceptionService.throwCodeException("该请求类型不支持多次请求" + appReq.getServiceKey());
//		}
//		return objResult;
//	}
	
//	private static void buildComplexAppResult(ComplexAppResultEntity objResult, 
//			BaseAppReqEntity appReq, ArrayList<String> serviceKeys){
//		if(objResult == null){
//			ExceptionService.throwCodeException("该请求不支持构建多请求结果对象");
//		}
//		// 初始化
//		JSONObject root = new JSONObject(true);
//		JSONArray jsonData = new JSONArray();
//		objResult.setJsonTarget(root);
//		objResult.setJsonTData(jsonData);
//		
//		// 构建PC请求结果集合(BaseAppResultEntity类型): appResults
//		ArrayList<BaseAppResultEntity> appResults = BaseResultService.getAppResultsWithoutProcess(serverReqs); 
//		objResult.setSubResults(appResults);
//	}
	/**
	 * 根据请求创建ComplexAppResultEntity结果对象，并初始化
	 * @param appReq
	 * @return
	 */
	private static ComplexAppResultEntity getComplexAppResult(BaseAppReqEntity appReq){
		ComplexAppResultEntity appResult = null;
		// 校验检查
		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(appReq.getServiceKey());
		if(objFile == null){
			LoggerAspect.logError("不支持该接口，未找到对应的配置文件" + appReq.getServiceKey());
			appResult = AppResultService.getErrComplexResultEntity(
					"不支持该接口，未找到对应的配置文件" + appReq.getServiceKey());
			return appResult;
		}
		ArrayList<String> serviceKeys = objFile.getAsReqServiceKeys();
		if(serviceKeys == null || serviceKeys.size() < 1){
			LoggerAspect.logError("请求参数错误！" + appReq.getServiceKey());
			appResult = AppResultService.getErrComplexResultEntity(
					"请求参数错误！" + appReq.getServiceKey());
			return appResult;
		}
		for(int i = 0; i < serviceKeys.size(); i++){
			String item = serviceKeys.get(i);
			if(item == null || item.equals("")){
				LoggerAspect.logError("请求参数为空或null！" + appReq.getServiceKey());
				appResult = AppResultService.getErrComplexResultEntity(
						"请求参数为空或null！" + appReq.getServiceKey());
				return appResult;
			}
		}
		// 构建请求报文: serverReqs, 都是未分页接口，拿到所有数据
		ArrayList<BaseServerReqEntity> serverReqs = ServerReqService.getServerReqs(serviceKeys, appReq); 
		if(serverReqs == null){
			ExceptionService.throwCodeException("无法构造请求列表！" + appReq.getServiceKey());
			return null;
		}
		// 发起请求，得到请求结果结合
		ArrayList<JSONObject> pcReqResults = null;
		try{
			pcReqResults = PCPostService.getPCResultJsons(serverReqs);
		}catch(Exception e){
			ExceptionService.throwCodeException("向PC发起请求失败：getComplexAppResult");
			return appResult;
		}
		try{
			appResult = BaseResultService.getAppResultEntity(appReq, serverReqs, pcReqResults);
		}catch(Exception e){
			ExceptionService.throwCodeException("格式化转化失败：getComplexAppResult");
			appResult = null;
		}
//		buildComplexAppResult(objResult, appReq, serviceKeys);
		return appResult;
	}
	
}
