package com.icbc.efrs.app.patchupdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.patchupdate.GetResMessage;

/**
 * <br>
 * <B>功能描述：</B><br>
 * <p>
 * 获取资源类型、资源包Sha1值
 * </p>
 * <br>
 * <B>使用方法和示例：</B><br>
 * <p>
 * </p>
 * 
 * @author kfzx-liuzs<br>
 *         中国工商银行软件开发中心
 * @version 1.00 2016-07-14
 */
public class GetResTypeServlet extends HttpServlet {

	private static final long serialVersionUID = -6337789063068723181L;

	public static final String TOTAL_UPTDATE_TYPE = "0";// 全量
	public static final String DIFF_UPTDATE_TYPE = "1";// 差量
	public static final String DELETE_UPTDATE_TYPE = "2";// 删除
	public static final String NOTHING_UPTDATE_TYPE = "3";// 不需要更新

	// 要返回的数据
	public static final String RES_TYPE_KEY = "resType";
	public static final String RES_NEW_VER_KEY = "resNewVer";
	public static final String RES_SIGN_KEY = "resSign";
	public static final String RES_TYPE_SIGN_KEY = "resTypeSign";
	public static final String RES_URI_KEY = "resUri";

	public static final String rootBundlePath = "currentInfo.txt";// 当前最新的模块路径名

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public GetResTypeServlet() {
		super();
	}

	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// servlet获得init-params

			ServletConfig config = this.getServletConfig();
			String implClass = config.getInitParameter("implClass");

			// 解析request的参数
			String requestJsonStr = request.getParameter("key");
			LoggerAspect.logInfo("接收到的请求json为：" + requestJsonStr);
			HashMap<String, String> requestJsonObj = JSON.parseObject(
					requestJsonStr,
					new TypeReference<HashMap<String, String>>() {
					});
			String resId = requestJsonObj.get("resId");
			String resSha256 = requestJsonObj.get("resSha256");
			String resVer = requestJsonObj.get("resVer");

			String userAgent = request.getHeader("User-Agent").toLowerCase();
			String platform = null;
			if (userAgent.indexOf("ios") > -1) {
				platform = "ios";
			} else if (userAgent.indexOf("android") > -1) {
				platform = "android";
			} else {
				LoggerAspect.logError("无法识别该platform，期待为ios或android = " + platform);
			}

			Class clazz = Class.forName(implClass);
			LoggerAspect.logInfo("将创建实例，参数为" + String.class + ", " +String.class + ", " +String.class);
			Constructor c = clazz.getConstructor(String.class, String.class,
					String.class);
			LoggerAspect.logInfo("将创建实例GetResMessage，参数为" + 
					resId + ", " +resVer + ", " +platform);
			GetResMessage getMessageHandler = (GetResMessage) c.newInstance(
					resId, resVer, platform);

			// String rootBundlePath=config.getInitParameter("rootBundlePath");

			// resNewVer
			// resType
			// resTypeSign
			// resSign
			// resUri

			HashMap<String, String> responseMap = new HashMap<String, String>();

			if (getMessageHandler.hasLocalRes() && platform != null) {
				String newVersion = getMessageHandler.getNewVersion();
				if (resVer != null && !resVer.equals("")) {
					String sha256Value = getMessageHandler.getCurVerSha256();

					if (!(getMessageHandler.isExistSha256File()
							&& getMessageHandler.isExistSignFile() && sha256Value
							.equals(resSha256))) {// 全量
						// isPatch=false;
						responseMap.put(RES_TYPE_KEY, TOTAL_UPTDATE_TYPE);
						responseMap.put(RES_TYPE_SIGN_KEY, getMessageHandler
								.getTypeSign(TOTAL_UPTDATE_TYPE));
						responseMap.put(RES_NEW_VER_KEY, newVersion);
						ArrayList<String> uriAndSign = getMessageHandler
								.getURIAndSign(newVersion, false);
						responseMap.put(RES_SIGN_KEY, uriAndSign.get(0));
						responseMap.put(RES_URI_KEY, uriAndSign.get(1));
					} else if (!resVer.equals(newVersion)) {
						// 走差量
						if (getMessageHandler.isExistPatchFile(newVersion)) {
							// isPatch=true;
							responseMap.put(RES_TYPE_KEY, DIFF_UPTDATE_TYPE);
							responseMap.put(RES_TYPE_SIGN_KEY,
									getMessageHandler
											.getTypeSign(DIFF_UPTDATE_TYPE));
							responseMap.put(RES_NEW_VER_KEY, newVersion);
							ArrayList<String> uriAndSign = getMessageHandler
									.getURIAndSign(newVersion, true);
							responseMap.put(RES_SIGN_KEY, uriAndSign.get(0));
							responseMap.put(RES_URI_KEY, uriAndSign.get(1));

						} else {
							responseMap.put(RES_TYPE_KEY, TOTAL_UPTDATE_TYPE);
							responseMap.put(RES_TYPE_SIGN_KEY,
									getMessageHandler
											.getTypeSign(TOTAL_UPTDATE_TYPE));
							responseMap.put(RES_NEW_VER_KEY, newVersion);
							ArrayList<String> uriAndSign = getMessageHandler
									.getURIAndSign(newVersion, false);
							responseMap.put(RES_SIGN_KEY, uriAndSign.get(0));
							responseMap.put(RES_URI_KEY, uriAndSign.get(1));
						}
					} else {
						// 本地没有要更新的资源，返回不需要更新的标志
						responseMap.put(RES_TYPE_KEY, NOTHING_UPTDATE_TYPE);
						responseMap.put(RES_TYPE_SIGN_KEY, getMessageHandler
								.getTypeSign(NOTHING_UPTDATE_TYPE));
					}

				} else {// DRD notes: 默认全量？？
					responseMap.put(RES_TYPE_KEY, TOTAL_UPTDATE_TYPE);
					responseMap.put(RES_TYPE_SIGN_KEY, getMessageHandler
							.getTypeSign(TOTAL_UPTDATE_TYPE));
					responseMap.put(RES_NEW_VER_KEY, newVersion);
					ArrayList<String> uriAndSign = getMessageHandler
							.getURIAndSign(newVersion, false);
					responseMap.put(RES_SIGN_KEY, uriAndSign.get(0));
					responseMap.put(RES_URI_KEY, uriAndSign.get(1));
				}

			} else {
				// 本地没有资源,返回删除
				responseMap.put(RES_TYPE_KEY, DELETE_UPTDATE_TYPE);
				responseMap.put(RES_TYPE_SIGN_KEY, getMessageHandler
						.getTypeSign(DELETE_UPTDATE_TYPE));
			}

			response.setContentType("text/plain;charset=utf-8");
			String str = JSON.toJSONString(responseMap);
			response.getWriter().write(str);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
