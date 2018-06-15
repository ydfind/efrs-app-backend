package com.icbc.efrs.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.alibaba.fastjson.JSON;
import com.icbc.efrs.app.utils.FileUtil;

public class ConsumerHTTP {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			request1();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 直接test
	private static void request() throws Exception {
		// 组织输入参数
//		String paramStr = "{\"ver\":\"2\",\"userId\":\"001100806\",\"bankId\":\"8B94459B9F1D4ECD\",\"type\":\"3\",\"source\":\"2\",\"language\":\"zh_CN\",\"ip\":\"84.232.45.110\",\"time\":\"20180102095024140\",\"data\":{\"accNo\":\"广州市堡利皮具有限公司3255\"}}";
		String paramStr = "123";
		String paramJson = JSON.toJSONString(new Object[] { paramStr});//注意这里必须json
		// 服务URL：path/服务名/版本/方法
//		String url = "http://122.26.13.159:16257/icbc/cocoa/json/com.icbc.efrs.dsf.compositeservice.MajorService/1.0/getMajorService";
		String url = "http://122.19.61.203:16257/icbc/cocoa/json/com.icbc.efrs.dsf.service.DateService/1.3/getDate";
		// 发起一次http调用
		String outJson = callHttpService(url, paramJson);
		if(outJson == null){
			System.out.println("获取接口失败");
		}else{
			System.out.println(outJson);
		}
	}
	
	private static void request1() throws Exception {
		// 组织输入参数
//		String paramStr = "{\"ver\":\"2\",\"userId\":\"001100806\",\"bankId\":\"8B94459B9F1D4ECD\",\"type\":\"3\",\"source\":\"2\",\"language\":\"zh_CN\",\"ip\":\"84.232.45.110\",\"time\":\"20180102095024140\",\"data\":{\"accNo\":\"广州市堡利皮具有限公司3255\"}}";
		String paramStr = "[" + FileUtil.getContent("D:/6.json") + "]";
//		String paramJson = JSON.toJSONString(new Object[] { paramStr});//注意这里必须json
		// 服务URL：path/服务名/版本/方法
//		String url = "http://122.26.13.159:16257/icbc/cocoa/json/com.icbc.efrs.dsf.compositeservice.MajorService/1.0/getMajorService";
		String url = "http://122.26.13.145:16257/icbc/cocoa/json/com.icbc.efrs.dsf.service.FahaiService/1.0/getFahai";
		// 发起一次http调用
//		String outJson = callHttpService(url, paramJson);
		paramStr = paramStr.replace("%key%", "中国工商银行");
		String outJson = callHttpService(url, paramStr);
		if(outJson == null){
			System.out.println("获取接口失败");
		}else{
			System.out.println(outJson);
		}
	}

	private static String callHttpService(String url, String paramJson) {
		String retStr = null;
		int returnCode = 0;
		int timeOut = 5000;
		HttpURLConnection connection = null;
		ByteArrayOutputStream baos = null;
		InputStream is = null;
		try {
			URL taskCreateServlet = new URL(url);
			connection = (HttpURLConnection) taskCreateServlet.openConnection();

			connection.setRequestMethod("POST");
			// http正文内,需要设置成true,默认是false

			byte[] dataBinary = paramJson.getBytes("UTF-8");

			connection.setDoOutput(true);
			// 设置是否从HttpURLConnection读入,默认情况下是true
			connection.setDoInput(true);
			connection.setRequestProperty("content-type",
					"application/json;chartset:UTF-8");
			connection.setRequestProperty("Content-Length", String
					.valueOf(dataBinary.length));
			connection.setConnectTimeout(timeOut);
			connection.connect();
			OutputStream output = connection.getOutputStream();
			output.write(dataBinary);
			output.flush();

			returnCode = connection.getResponseCode();
			// 获取返回的数据
			if (returnCode == 200) {
				is = connection.getInputStream();
				baos = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				String reponseStr = baos.toString("utf8");
				retStr = reponseStr.toString();
				System.out.println("收到的字符串为" + retStr);
			}else{
				// 非200认为是异常
				// 请在以下部分进行异常处理
				System.out.println(returnCode+">>>>>"+connection.getResponseMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (connection != null) {
				connection.disconnect();
			}
		}
		return retStr;
	}

}
