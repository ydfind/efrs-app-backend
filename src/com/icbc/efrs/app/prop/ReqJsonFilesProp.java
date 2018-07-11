package com.icbc.efrs.app.prop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.FileUtil;
/**
 * 与请求相关的json配置文件集
 *
 */
public class ReqJsonFilesProp {
	private static final String cfgName = "json" + File.separator;
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
//			LoggerAspect.logInfo("文件名称：" + filenames.get(i));
			// 将serviceKey替换成中文
			String oldServiceKey = objFile.getReqKey();
			String serviceKey = ServiceKeysProp.getServiceKey(oldServiceKey);
			if(!serviceKey.equals(oldServiceKey)){
				objFile.setReqKey(serviceKey);
			}
			jsonFileMap.put(objFile.getReqKey(), objFile);
//			ServiceKeysProp.changeFileName(objFile.getFilename());
		}
//		TestServiceKeys();
	}
	
	public static String getPath(){
		String dir = ServerProp.getAppServerPath() + cfgName;
		LoggerAspect.logInfo("请求json的目录名称为：" + dir);
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
			ExceptionService.throwCodeException("无法找到请求对应的配置文件 = " + key);
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
    
//    public static void TestServiceKeys(){
//    	JSONObject obj = new JSONObject(true);
//    	int i = 1000; 
//    	for(String key: jsonFileMap.keySet()){
//    		obj.put(String.valueOf(i), key);
//    		i++;
//    	}
//    	String content = obj.toJSONString();
//    	FileUtil.writeFile("D:/servicekeys.json", content);
//    	
//    }
}
