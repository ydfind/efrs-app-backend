package com.icbc.efrs.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;

import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.utils.CoreUtils;
import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.utils.JSONUtil;
import com.icbc.efrs.app.utils.ResultUtils;

// 每一个大的接口，里面包含多个业务函数
public class MyTestService {
	
	private static MyTestJsonDisplayEntity getTestDisplayJson(AppResultBaseEntity objSourceEntity, AppReqBaseEntity requestEntity) {
		// 初始化
		String[] TargetKeys = {"BODY", "COURT", "D_DATA_DATE", "PNAME", "GGTYPE"};
		MyTestJsonDisplayEntity objTargetEntity = new MyTestJsonDisplayEntity();
		String key; 
		int i;
		JSONObject objTargetJson, objSourceJson;
		
		objSourceJson = objSourceEntity.getJsonSource();
		objTargetJson = objTargetEntity.getJsonObject();
		// 添加查询是否成功
		// 若后台反应数据超时，则应该规定出错json返回-----------------
		key = "msg";
		objTargetJson.put(key, objSourceJson.get(key));
		// 是否是企业年报，根据App发的请求决定；
		key = "listtype";
				
		
//		objTargetJson.put(key, requestEntity.getTabletype() == 1);
		
		
		
		
		objTargetJson.put(key, 0);
		// 添加总条数、总页数、当前页数
		key = "totalNum";
		int totalNum;
		try {
			totalNum = Integer.parseInt((String)objSourceJson.get(key));
		}
		catch(NumberFormatException e){
			totalNum = 0;
			e.printStackTrace();
		}
		key = "总条数";
		objTargetJson.put(key, String.valueOf(totalNum));
//		key = "totalPage";
		key = "总页数";
		objTargetJson.put(key, String.valueOf(Math.round(totalNum / requestEntity.getSize())));
//		key = "CurrentPage";
		key = "当前页数";
		objTargetJson.put(key, String.valueOf(1));
		// 处理主数据data（原result节点）	
		JSONArray objTemp, objTarget;
		objTarget = new JSONArray();
		objTemp = objSourceEntity.getJsonSource().getJSONArray("result");
		ProcessJsonService.CopyByKeys(TargetKeys, objTemp, objTarget);
		key = "data";
		objTargetJson.put(key, objTarget);	
		// 翻译
//		ProcessJsonService.ProcessJson(objTarget); 	
		// 特殊字符处理
		return objTargetEntity; 
	}
	
	public static MyTestJsonDisplayEntity getTestDisplayJson(String filename, AppReqBaseEntity requestEntity) {
		// 1。初始化
		int i;
		MyTestJsonDisplayEntity objTargetEntity;
		AppResultBaseEntity objSourceEntity = new AppResultBaseEntity();
		// 1）得到数据源: 需要初始化的校验，PC服务器端返回的数据可能失败；
		String ret = FileUtil.getContent(filename);
//		if (CoreUtils.nullSafeSize(ret) == 0) {
//			return ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();
//		}
		objSourceEntity.setJsonSource(JSON.parseObject(ret, Feature.OrderedField));
		// 2 处理数据源 -> 目标数据源
		objTargetEntity = getTestDisplayJson(objSourceEntity, requestEntity);		
		// 3返回结果
		return objTargetEntity;
	}
}
