package com.icbc.efrs.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.utils.CoreUtils;
import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.utils.ResultUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.prop.AppServerProp;
import com.icbc.efrs.app.prop.TransFilesProp;
import com.icbc.efrs.app.serverreq.ServerReqBaseEntity;
import com.icbc.efrs.app.service.*;
import com.icbc.efrs.app.domain.*;

@RestController
@RequestMapping("/app")
@Api(tags="移动端api服务")
public class AppController {

//	@PostMapping(value = "/Testjson/{name}", produces = "application/json;charset=utf-8")
	@PostMapping(value = "/Testjson/{name}", produces = "text/xml;charset=utf-8")
	public String getTestJson(
			@PathVariable(value = "name", required = false) String name, @RequestBody String body,
			HttpServletRequest request) {

		String realPath = request.getSession().getServletContext().getRealPath(
				"/");
		System.out.println("java web的webcontent文件夹目录为：" + realPath);
		if (realPath != null && !realPath.endsWith(File.separator)) {
			realPath = realPath + File.separator;
			System.out.println("路径不包含斜杠，需要加上斜杠：" + realPath);
		}

		System.out.println("接收body为 = " + body);
		
		
//		AppReqBaseEntity objAppRqt = new AppReqBaseEntity(body);
		AppReqBaseEntity objAppRqt = new AppReqBaseEntity();
		
		
		
		// TODO 临时写死取文件，后续请调整
		String filePath = realPath + "json" + File.separator + name + ".json";
		System.out.println("需要找到的文件为：" + filePath);
		
		MyTestJsonDisplayEntity objEntity = MyTestService.getTestDisplayJson(filePath, objAppRqt);
//		// 翻译
//		JSONArray jsonObject = objEntity.getJsonObject().getJSONArray("data");
//		
//		TranslateJsonService.TranslateJsonKey(jsonObject); 
		String Result = JSON.toJSONString(objEntity.getJsonObject());
		
//		String ret = FileUtil.getContent(filePath);
//		if (CoreUtils.nullSafeSize(ret) == 0) {
//			return ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();
//		}
//		JSONObject objJson = JSON.parseObject(ret);
//		objJson.put("MyJsonTest", "Success");
//		String Result = JSON.toJSONString(objJson);
		
		return Result;
	}
	
	/**
	 * 根据name上下文根，获取结果json
	 * 
	 * @param paramStr
	 * @return
	 * @throws IOException
	 */
	
//	@PostMapping(value = "/MyJson/jsonpost", produces = "text/xml;charset=utf-8")
	@PostMapping(value = "/MyJson/jsonpost", produces = "applicatoin/json;charset=utf-8")
	@ApiOperation(httpMethod = "POST", value = "请求后端Json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getGetServerJson(@RequestBody String body, HttpServletRequest request) {
		// TODO 从request解析json，调用相应接口服务进行json处理，调用字段前置服务，调用翻译服务
		System.out.println("---------------接收body为 = " + body);
//		System.out.println("工作目录为：" + AppServerProp.getAppServerPath());
		ServerReqBaseEntity serverReq = ReqBaseService.getServerReqEntity(body);
		String result = PCPostService.callHttpService(serverReq.getPcReqUrl(), serverReq.getPCServerPost());
		if(result == null){
			System.out.println("获取接口失败");
			result = ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();	
		}
		result = JsonFormatBaseService.jsonParse(result);
		JSONObject obj = JSON.parseObject(result, Feature.OrderedField);
		result = obj.toString();
		
		AppResultBaseEntity appResult = JsonFormatBaseService.getAppResultEntity(serverReq, result);
		result = appResult.getAppResultStr();	
		return result;
	}
	
	
}
