package com.icbc.efrs.app.prop;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.icbc.efrs.app.service.ExceptionService;

public class AppServerProp implements ServletContextListener {
	private static String appServerPath;
	public static String getAppServerName(){
		return "efrsapp";
	}
	
	public static String getAppServerPath(){
		return appServerPath;		
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
		}
	}
}
