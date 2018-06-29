package com.icbc.efrs.app.prop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.FileUtil;
/**
 * 与请求相关的json配置文件集
 *
 */
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
		String dir = ServerProp.getAppServerPath() + cfgName;
		System.out.println("请求json的目录名称为：" + dir);
		return dir;
	}

	public static ReqJsonFileEntity getJsonFileEntity(String key){
		if(key == null || key.equals("")){
			ExceptionService.throwCodeException("请求key为空，无法找到请求对应的配置文件");
			return null;
		}
		else if(jsonFileMap.containsKey(key))
			return jsonFileMap.get(key);
//		else if(key.equals(ReqIntfEnums.CompanyQuery.getDesc())){
//			return null;// 首页查询默认没有的
//		}
		else{
			ExceptionService.throwCodeException("无法找到请求对应的配置文件");
			return null;
		}
	}
    public static ReqIntfEnums getReqIntfEnums(String serviceKey){
    	if(jsonFileMap.containsKey(serviceKey))
			return jsonFileMap.get(serviceKey).getReqType();
//		else if(serviceKey.equals(ReqIntfEnums.CompanyQuery.getDesc())){
//			return ReqIntfEnums.CompanyQuery;// 首页查询默认没有的
//		}
		else{
			ExceptionService.throwCodeException("无法找到请求对应的配置文件");
			return ReqIntfEnums.ErrIntf;
		}
		
	}
}
