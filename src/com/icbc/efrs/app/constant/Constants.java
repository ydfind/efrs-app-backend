package com.icbc.efrs.app.constant;

public interface Constants {
	public final static String ENCODING_DEF = new String("UTF-8");
	// APP请求中使用到常量
	public static final Integer REQ_DEF_PAGE = 1;
	public static final Integer REQ_DEF_SIZE = 10;
	public static final String REQ_NAME_BANKID = new String("bankId");
	public static final String REQ_NAME_USERID = new String("userId");
	public static final String REQ_NAME_PAGE = new String("page");
	public static final String REQ_NAME_SIZE = new String("size");
	public static final String REQ_NAME_KEY = new String("key");
	public static final String REQ_NAME_ZS_ZMXX = new String("照面信息");
	public static final String REQ_NAME_COMPLEX_SSWF_ZS = new String("税收违法ZS");
	public static final String REQ_NAME_COMPLEX_SSWF_FX = new String("税收违法FX");
	// APP请求结果
	public static final Integer RES_DEF_START_PAGE = 1;
	public static final String RES_NAME_DATA = new String("data");
	public static final String RES_DEF_ERROR_CODE = "-405";
	public static final String RES_DEF_ERROR_DES = "查询过程中出现异常";
	public static final String RES_QUERY_NO_INFO = "没有查到满足条件的信息";
	// 翻译文件
	public static final String TRANS_FX_NAME_DATA = new String("data");
}
