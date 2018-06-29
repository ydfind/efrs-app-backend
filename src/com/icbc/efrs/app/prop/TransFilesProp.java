package com.icbc.efrs.app.prop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.BaseAppReqEntity;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.domain.TransFileEntity;
import com.icbc.efrs.app.service.BaseReqService;
import com.icbc.efrs.app.service.BaseResultService;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.service.PCPostService;
import com.icbc.efrs.app.utils.FileUtil;
/**
 * 翻译json配置文件
 *
 */
public class TransFilesProp {
	private static final String cfgName = "json/";
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
		}
		initFXTrans();
	}
	
	private static void initFXTrans(){
		String fxPath = getPath() + fxFilename;
		String content = FileUtil.getContent(fxPath);
		JSONObject obj = PCPostService.getHttpServiceJson(PCServerUrlsProp.getFengXianTransUrl(), content);
		if(obj != null){
			TransFileEntity objFile = new TransFileEntity(obj);
			fileMap.put(fxKeyname, objFile);
		}else{
			System.out.println("----获取接口失败：initFXTrans");
		}
	}
	
	public static void addFile(String filename){
		TransFileEntity objFile = new TransFileEntity(getPath() + filename);
		System.out.println("-----------翻译文件名称：" + filename);
		String reqKey = objFile.getReqKey();
		fileMap.put(reqKey, objFile);	
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
		JSONObject obj = BaseReqService.getFXTransPCResultJson(appResult, appReq, content);
		if(obj != null){
			objFile = new TransFileEntity(obj);
		}
		return objFile;
		
	}
}
