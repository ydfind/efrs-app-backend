package com.icbc.efrs.app.prop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.service.*;
/**
 * 翻译json配置文件
 *
 */
public class TransFilesProp {
	
	private static final String cfgName = "json" + File.separator;
	private static final String endStr = "apptrans";
	private static final String fxFilename = "FengXianTransReq.json";
	private static final String fxKeyname = "fengxian";
	private static Map<String, TransFileEntity> fileMap;
	
	static{
		init();
	}
	
	public static void init(){
		fileMap = new HashMap<String, TransFileEntity>();
		
        ArrayList<String> filenames = FileUtil.getFilesEndWith(getPath(), endStr);
		
		for(int i = 0; i < filenames.size(); i++){
			addFile(filenames.get(i));
//			ServiceKeysProp.changeFileName(getPath() + filenames.get(i));
		}
	}
	
	public static void addFile(String filename){
		TransFileEntity objFile = new TransFileEntity(getPath() + filename);
		String oldServiceKey = objFile.getReqKey();
		String serviceKey = ServiceKeysProp.getServiceKey(oldServiceKey);
		if(!serviceKey.equals(oldServiceKey)){
			objFile.setReqKey(serviceKey);
		}
		fileMap.put(serviceKey, objFile);	
	}
	
	public static String getPath(){
		String dir = ServerProp.getAppServerPath() + cfgName;
		return dir;
	}

	public static Map<String, TransFileEntity> getFileMap() {
		return fileMap;
	}
	
	public static TransFileEntity getTransFileEntity(String key){
		if(fileMap.containsKey(key))
			return fileMap.get(key);
		else{
			ExceptionService.throwCodeException("无法找到请求对应的翻译配置文件");
			return null;
		}
	}
	
	public static TransFileEntity getFXTransFileEntity(BaseAppResultEntity appResult, BaseAppReqEntity appReq){
		TransFileEntity objFile = null;
		String fxPath = getPath() + fxFilename;
		String content = FileUtil.getContent(fxPath);
		JSONObject obj = PCPostService.getFXTransPCResultJson(appResult, appReq, content);
		if(obj != null){
			objFile = new TransFileEntity(obj);
		}
		return objFile;
	}
	
	private static void copyMap(Map<String, String> source, Map<String, String> target){
		for(String key: source.keySet()){
			if(target.containsKey(key)){
				if(target.get(key).equals(source.get(key))){
					LoggerAspect.logWarn("翻译冲突！" + key + ":" + 
							target.get(key) + " and " + source.get(key));
				}else{
					ExceptionService.throwCodeException("翻译冲突！" + key + ":" + 
							target.get(key) + " and " + source.get(key));
				}
			}else{
				target.put(key, source.get(key));
			}
		}
	}
	
	public static Map<String, String> getComplexTransMap(ComplexAppResultEntity appResult, 
			BaseAppReqEntity appReq){
		Map<String, String> map = new HashMap<String, String>();
		String serviceKey = appReq.getServiceKey();
		if(serviceKey.equals("")){
			ExceptionService.throwCodeException("该请求关键字不存在 = " + serviceKey);
			return map;
		}
		ReqJsonFileEntity complexFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
		ArrayList<String> serviceKeys = complexFile.getAsReqServiceKeys();
		for(int i = 0; i < serviceKeys.size(); i++){
			String key = serviceKeys.get(i);
			TransFileEntity objFile = null;
			if(fileMap.containsKey(key)){
				objFile = fileMap.get(key);
			}else{
				ReqIntfEnums type = ReqJsonFilesProp.getReqIntfEnums(key);
				// 风险的
				if(type == ReqIntfEnums.FXWithParam || type == ReqIntfEnums.FXWithParams){
					objFile = getFXTransFileEntity(appResult.getSubResults().get(i), appReq);
				}
			}
			if(objFile != null){
				copyMap(objFile.getKeyMap(), map);
			}else{
				LoggerAspect.logWarn("找不到该请求对应翻译 = " + key);
			}
		}
		// 复杂类型本身的翻译文件
		if(fileMap.containsKey(serviceKey)){
			copyMap(fileMap.get(serviceKey).getKeyMap(), map);
		}
		return map;
	}

}
