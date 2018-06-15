package com.icbc.efrs.app.service;

import com.icbc.efrs.app.exception.CodeException;

public class ExceptionService {
	public static void throwCodeException(String errStr){
		try{
			throw new CodeException("---------程序员代码bug或约定不符合错误: " + errStr);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
