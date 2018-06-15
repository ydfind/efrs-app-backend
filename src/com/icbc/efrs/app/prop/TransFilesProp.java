package com.icbc.efrs.app.prop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.domain.TransFileEntity;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.FileUtil;

public class TransFilesProp {
	private static final String cfgName = "json/";
	private static final String endStr = "apptrans";
	private static Map<String, TransFileEntity> fileMap;
	
	static{
		init();
	}
	
	public static void init(){
		fileMap = new HashMap<String, TransFileEntity>();
		
        ArrayList<String> filenames = FileUtil.getFilesEndWith(getPath(), endStr);
		
		for(int i = 0; i < filenames.size(); i++){
			addFile(filenames.get(i));
//			TransFileEntity objFile = new TransFileEntity(getPath() + filenames.get(i));
//			System.out.println("-----------翻译文件名称：" + filenames.get(i));
//			String reqKey = objFile.getReqKey();
//			fileMap.put(reqKey, objFile);
		}
	}
	
	public static void addFile(String filename){
		TransFileEntity objFile = new TransFileEntity(getPath() + filename);
		System.out.println("-----------翻译文件名称：" + filename);
		String reqKey = objFile.getReqKey();
		fileMap.put(reqKey, objFile);	
	}
	
	public static String getPath(){
		String dir = AppServerProp.getAppServerPath() + cfgName;
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
}
