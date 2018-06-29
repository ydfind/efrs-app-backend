package com.icbc.efrs.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.prop.*;
import com.icbc.efrs.app.service.*;
import com.icbc.efrs.app.domain.*;

@RestController
@RequestMapping("/app")
@Api(tags="移动端api服务")
public class AppController {
	
	/**
	 * 处理通常的App请求：法海、模糊查询、风险等
	 * @param appReq 请求对象
	 * @return 返回请求结果string
	 */
	private String getAppResultStr(BaseAppReqEntity appReq){
		String result = null;

		BaseServerReqEntity serverReq = ServerReqService.getServerReqEntity(appReq);
		if(serverReq != null)
		    result = BaseReqService.getPcResultStr(serverReq);	
		try{
			BaseAppResultEntity appResult = BaseResultService.getAppResultEntity(appReq, serverReq, result);
//			// DRD: 检查data下节点是否有序
//			if(appResult.getJsonTData() instanceof JSONObject){
//				JSONObject obj = (JSONObject)appResult.getJsonTData();
//				Map<String, Object> objMap = obj.getInnerMap();
//				if(!(objMap instanceof LinkedHashMap)){
//					ExceptionService.throwCodeException("JSONObject不是LinkedHashMap类型无法排序=" + 
//							obj.getClass() + "--" + objMap.getClass() + "--");
//				}
//			}
//			// DRD:首页模糊查询不带data节点，特殊处理--------已改为添加到data节点里面
//			if(appReq.getIntfType() == ReqIntfEnums.FuzzyQuery){
//				appResult.getJsonTarget().remove(BaseAppResultEntity.NAME_DATA);
//			}
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
	private String getCompanyQueryStr(BaseAppReqEntity appReq){
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
	    root.put("data", data);
	    result = root.toJSONString();
		return result;
	}
	/**
	 * 根据name上下文根，获取结果json
	 * 
	 * @param paramStr
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/json/jsonpost", produces = "applicatoin/json;charset=utf-8")
	@ApiOperation(httpMethod = "POST", value = "请求后端Json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getServerJson(@RequestBody String body, HttpServletRequest request) {
		// TODO 从request解析json，调用相应接口服务进行json处理，调用字段前置服务，调用翻译服务
		System.out.println("---------------接收body为 = " + body);
		String result = null;
		// body解析出请求类型，顺便校验格式
		BaseAppReqEntity appReq = AppReqService.parse(body);
		switch(appReq.getIntfType()){
		case FaHai:;
		case FuzzyQuery:
		case FXWithParam:
		case FXWithParams:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
			result = getAppResultStr(appReq);
			break;
		case CompanyQuery:
			result = getCompanyQueryStr(appReq);
			break;
		default: 
			ExceptionService.throwCodeException("无法识别此接口类型-getServerJson");
		}	
		System.out.println("---返回的结果为：" + result);
		return result;
	}
	// 用于检查产生的报文是否正确
	private String testServerPostStr(BaseAppReqEntity appReq){
		BaseServerReqEntity serverReq = ServerReqService.getServerReqEntity(appReq);
		String result = serverReq.getPCServerPost();
		return result;
	}
	// 用于检查处理pc返回的结果处理是否正确
	@PostMapping(value = "/testjson/jsonpost", produces = "applicatoin/json;charset=utf-8")
	@ApiOperation(httpMethod = "POST", value = "请求后端Json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String testServerJson(@RequestBody String body, HttpServletRequest request) {
		// TODO 从request解析json，调用相应接口服务进行json处理，调用字段前置服务，调用翻译服务
		String url = "http://122.26.13.145:16257/icbc/cocoa/json/com.icbc.efrs.dsf.chinadaas.CdDataService/1.1/getCdDataService";
		String content = FileUtil.getContent("D:/test.json");
		JSONObject obj = JSON.parseObject(content);
		content = "[" + obj.toString() + "]"; 
		String result = PCPostService.callHttpService(url, content);
		if(result == null){
			System.out.println("----获取接口失败：testServerJson");
		}
		return result;
	}
	// 用于重载翻译配置文件
	@GetMapping(value = "/testjson/test", produces = "applicatoin/json;charset=utf-8")
	public String testJsonFiles() {
		// TODO 从request解析json，调用相应接口服务进行json处理，调用字段前置服务，调用翻译服务
		Map<String, TransFileEntity> map = TransFilesProp.getFileMap();
		for(String key: map.keySet()){
			System.out.println("-------------------------" + key + "------------");
			Map<String, String> obj = map.get(key).getKeyMap();
			for(String key1: obj.keySet()){
				System.out.println("('key', 'value')" + "('" + key1 + "', '" + obj.get(key1) + "'");
			}
		}
		return "";
	}
	
}
