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

public class TestAppController {

	/**
	 * 根据name上下文根，获取结果json
	 * 
	 * @param paramStr
	 * @return
	 * @throws IOException
	 */
	
	@PostMapping(value = "/TestJson/jsonpost", produces = "text/xml;charset=utf-8")
//		@PostMapping(value = "/MyJson/jsonpost", produces = "applicatoin/json;charset=utf-8")
	@ApiOperation(httpMethod = "POST", value = "请求后端Json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getGetServerJson(@RequestBody String body, HttpServletRequest request) {
		// TODO 从request解析json，调用相应接口服务进行json处理，调用字段前置服务，调用翻译服务
		System.out.println("---------------接收body为 = " + body);
//			System.out.println("工作目录为：" + AppServerProp.getAppServerPath());
		ServerReqBaseEntity serverReq = ReqBaseService.getServerReqEntity(body);
//			String result = PCPostService.callHttpService(serverReq.getPcReqUrl(), serverReq.getPCServerPost());
		String result = null;
//			String result = null;
		if(result == null){
			System.out.println("获取接口失败");
			result = ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();	
			if(true){// 本地测试使用
				String ret = FileUtil.getContent("D:/文档/法海/裁判文书-报文.json");
				result = ret;
				
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher matd = p.matcher(result);
				result = matd.replaceAll("");
			}
		}
		result = JsonFormatBaseService.jsonParse(result);
		JSONObject obj = JSON.parseObject(result, Feature.OrderedField);
		result = obj.toString();
		
		AppResultBaseEntity appResult = JsonFormatBaseService.getAppResultEntity(serverReq, result);
		result = appResult.getAppResultStr();
////			result = JsonFormatBaseService.jsonParse(result);
//			if(!JsonFormatBaseService.invalidateJsonObject(result)){
//				// 返回的不是标准jsonobject格式
//				System.out.println("不是标准格式-----------------------------------");
//			}else{
//				System.out.println("是标准格式-----------------------------------");
//			}
//			System.out.println("-----------------------------------" + result);
		
		return result;
	}
	
	@GetMapping(value = "/TestJson/test", produces = "applicatoin/json;charset=utf-8")
	public String getTestJson() {
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
