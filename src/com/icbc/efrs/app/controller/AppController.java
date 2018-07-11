package com.icbc.efrs.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.prop.*;
import com.icbc.efrs.app.service.*;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.*;

@RestController
@RequestMapping("/app")
@Api(tags="移动端api服务")
public class AppController {
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
		LoggerAspect.logInfo("提示：接收body为 = " + body);
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
		case ZSWithParam:
		case PatentInfo:
			result = AppService.getAppResultStr(appReq);
			break;
		case CompanyQuery:
			result = AppService.getCompanyQueryStr(appReq);
			break;
		case AbnormalManage:
		case QualityCertification:
		case TeleFraud:
		case TaxIllegal:
			result = AppService.getComplexResultStr(appReq);
			break;
		default: 
			ExceptionService.throwCodeException("无法识别此接口类型-getServerJson = " + appReq.getIntfType());
		}	
		LoggerAspect.logInfo("提示：返回的结果为：" + result);
		return result;
	}
	/**
	 * 根据name上下文根，获取结果json
	 * 
	 * @param paramStr
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/json/patchupdate", produces = "applicatoin/json;charset=utf-8")
	@ApiOperation(httpMethod = "POST", value = "请求后端Json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getPatchupdateJson(@RequestBody String body, HttpServletRequest request) {
		// 差量更新相关
		String result = "";
		result = PatchupdateService.getPatchupdateStr(body, request);
		if(result == null || result.equals("")){
			LoggerAspect.logError("版本检查失败");
		}
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
			LoggerAspect.logError("----获取接口失败：testServerJson");
		}
		return result;
	}
	
	// 用于重载翻译配置文件
	@GetMapping(value = "/testjson/test", produces = "applicatoin/json;charset=utf-8")
	public String testJsonFiles() {
		// TODO 从request解析json，调用相应接口服务进行json处理，调用字段前置服务，调用翻译服务
		Map<String, TransFileEntity> map = TransFilesProp.getFileMap();
		for(String key: map.keySet()){
			Map<String, String> obj = map.get(key).getKeyMap();
			for(String key1: obj.keySet()){
				LoggerAspect.logInfo("('key', 'value')" + "('" + key1 + "', '" + obj.get(key1) + "'");
			}
		}
		return "";
	}
	
}
