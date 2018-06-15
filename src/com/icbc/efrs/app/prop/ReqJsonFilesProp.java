package com.icbc.efrs.app.prop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.FileUtil;

public class ReqJsonFilesProp {
	private static final String cfgName = "json/";
	private static final String endStr = "appserver.json";
	public static Map<String, ReqJsonFileEntity> jsonFileMap;
	
	static{
		init();
	}
	
	public static void init(){
		jsonFileMap = new HashMap<String, ReqJsonFileEntity>();
		
        ArrayList<String> filenames = FileUtil.getFilesEndWith(getPath(), endStr);
		
		for(int i = 0; i < filenames.size(); i++){
			ReqJsonFileEntity objFile = new ReqJsonFileEntity(getPath() + filenames.get(i));
			System.out.println("文件名称：" + filenames.get(i));
			jsonFileMap.put(objFile.getReqKey(), objFile);
		}
	}
	
	public static String getPath(){
		String dir = AppServerProp.getAppServerPath() + cfgName;
		System.out.println("请求json的目录名称为：" + dir);
		return dir;
	}

	public static ReqJsonFileEntity getJsonFileEntity(String key){
		if(jsonFileMap.containsKey(key))
			return jsonFileMap.get(key);
		else{
			ExceptionService.throwCodeException("无法找到请求对应的配置文件");
			return null;
		}
	}
}
