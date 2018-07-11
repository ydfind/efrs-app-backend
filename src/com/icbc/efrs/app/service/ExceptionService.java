package com.icbc.efrs.app.service;

import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.exception.CodeException;
/**
 * 程序运行时与约定不符的报错处理
 *
 */
public class ExceptionService {
	public static void throwCodeException(String errStr){
		try{
			LoggerAspect.logError("程序员代码bug或约定不符合错误: " + errStr);
			throw new CodeException("---------程序员代码bug或约定不符合错误: " + errStr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
