package com.icbc.efrs.app.prop;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.service.ExceptionService;
/**
 * 服务器配置类
 *
 */
public class ServerProp implements ServletContextListener {
	private static String appServerPath;
	
	public static String getAppServerPath(){
		return appServerPath;		
	}
	
	public static String getGlobalDateFormatStr(){
		return "yyyyMMddHHmmssSSS";
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		appServerPath = arg0.getServletContext().getRealPath("/");
		if(appServerPath == null){
			ExceptionService.throwCodeException("无法得到webapp目录");	
		}else{
			if(!appServerPath.endsWith(File.separator)){
				appServerPath = appServerPath + File.separator;
			}
		}
		LoggerAspect.logInfo("服务器目录：" + appServerPath);
	}
}
