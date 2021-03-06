package com.icbc.efrs.app.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.prop.PCServerUrlsProp;
/**
 * 向PC发请求，并返回结果
 *
 */
public class PCPostService {
	
	private static JSONObject getResultJson(String result, boolean needparse){
		JSONObject obj = null;
		if(result != null){
			if(needparse){
				result = BaseResultService.jsonParse(result);	
			}
			obj = JSON.parseObject(result, Feature.OrderedField);
		}else{
			LoggerAspect.logWarn("----PC端返回的结果为null，请求失败：getResultJson");
		}
		return obj;
	}

	/**
	 * 向PC发起请求，并返回结果
	 * @param url post的地址
	 * @param paramJson post的内容
	 * @return 请求PC得到的结果：string类型
	 */
	public static String callHttpService(String url, String paramJson) {
		LoggerAspect.logInfo("请求的url：" + url);
		LoggerAspect.logInfo("请求的json为：" + paramJson);
		String retStr = null;
		int returnCode = 0;
		int timeOut = 2000;
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
				LoggerAspect.logInfo("收到的字符串为" + retStr);
			}else{
				// 非200认为是异常
				// 请在以下部分进行异常处理
				LoggerAspect.logError(returnCode+">>>>>"+connection.getResponseMessage());
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

	/**
	 * 向PC发起请求，并返回结果
	 * @param url post的地址
	 * @param paramJson post的内容
	 * @return 请求PC得到的结果：JSONObject类型
	 */
	public static JSONObject getHttpServiceJson(String url, String paramJson) {
		JSONObject obj = null;
		String result = callHttpService(url, paramJson);
		obj = getResultJson(result, true);
		return obj;
	}

	/**
	 * 同一url在第一次请求失败后，不再发起请求，默认失败；
	 * 同一请求内容只向PC发送一次；
	 * @param serverReqs 向PC发起的请求集合
	 * @return 向PC发起的请求的结果集合
	 */
	public static ArrayList<String> getPCResultStrs(ArrayList<String> urls, ArrayList<String> paramJsons){
		// url和paramJson的数量需一致
		if(urls == null || paramJsons == null || urls.size() != paramJsons.size()){
			return null;
		}
		ArrayList<String> pcResults = new ArrayList<String>();
		ArrayList<String> failedUrls = new ArrayList<String>();
		Map<String, Integer> actualReqs = new HashMap<String, Integer>();
		for(int i = 0; i < urls.size(); i++){
			String url = urls.get(i);
			String paramJson = paramJsons.get(i);
			String pcResult = "";
			if(url != null && paramJson != null && !url.equals("") && !paramJson.equals("")){
				String reqKey = url + "#" + paramJson;// 请求唯一标志
				if(actualReqs.containsKey(reqKey)){
					int matchKey = actualReqs.get(reqKey);
					// 校验
					if(matchKey >= pcResults.size() || matchKey < 0){
						ExceptionService.throwCodeException("无法找到相似请求数据");
					}
					pcResult = pcResults.get(matchKey);
				}else if(!failedUrls.contains(url)){// 同一地址请求失败后，后续请求默认都是失败
					// 重新发送请求
		    		pcResult = callHttpService(url, paramJson);
				    if(pcResult == null){
				    	failedUrls.add(url);// 该url下次不再查询
				    	pcResult = "";
				    }
				    actualReqs.put(reqKey, i);	
				}
			}
			pcResults.add(pcResult);
		}
		return pcResults;
	}

	/**
	 * 同一url在第一次请求失败后，不再发起请求，默认失败；
	 * 同一请求内容只向PC发送一次；
	 * @param serverReqs 向PC发起的请求集合
	 * @return 向PC发起的请求的结果集合
	 */
	public static ArrayList<JSONObject> getPCResultJsons(ArrayList<String> urls, ArrayList<String> paramJsons){
		ArrayList<String> strs = getPCResultStrs(urls, paramJsons);
		if(strs == null){
			return null;
		}
		ArrayList<JSONObject> jsons = new ArrayList<JSONObject>();
		for(int i = 0; i < strs.size(); i++){
			jsons.add(getResultJson(strs.get(i), true));	
		}
		return jsons;
	}
	
	public static ArrayList<JSONObject> getPCResultJsons(ArrayList<BaseServerReqEntity> serverReqs){
		if(serverReqs == null){
			return null;
		}
		// 得到请求结果
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<String> paramJsons = new ArrayList<String>();
		for(int i = 0; i < serverReqs.size(); i++){
			BaseServerReqEntity serverReq = serverReqs.get(i);
			// 构造请求的url、post参数
			if(serverReq != null){
				urls.add(serverReq.getPcReqUrl());
				paramJsons.add(serverReq.getPCServerPost());
			}else{
				urls.add(null);
				paramJsons.add(null);
			}
		}
		return PCPostService.getPCResultJsons(urls, paramJsons);
	}

	public static String getPcResultStr(BaseServerReqEntity serverReq){
	String result = null;
	if(serverReq == null)
		return result;
	// body解析出请求类型，校验格式
	String url = serverReq.getPcReqUrl();
	String str = serverReq.getPCServerPost();
	result = PCPostService.callHttpService(url, str);
//	// DRD: 风险没有数据，先如此处理
//	if(ReqJsonFilesProp.getReqIntfEnums(serverReq.getServiceKey()) == ReqIntfEnums.FXWithParam ||
//			ReqJsonFilesProp.getReqIntfEnums(serverReq.getServiceKey()) == ReqIntfEnums.FXWithParams){
//		LoggerAspect.logInfo("目前没有风险数据，先伪造部分数据");
//		String content = FileUtil.getContent("D:/风险3.json");
//		result = content;
//	}
	
	if(result == null){
		LoggerAspect.logWarn("----获取接口失败：getPcResultStr");
	}
	else{
		result = BaseResultService.jsonParse(result);
	}
	return result;
	}
	
	public static JSONObject getFXTransPCResultJson(BaseAppResultEntity appResult, BaseAppReqEntity appReq, String content){
		JSONObject result = null;
		String url = PCServerUrlsProp.getFengXianTransUrl();
		String str = ServerReqService.parseFXTransPost(appResult, appReq, content);
		if(str == null || str.equals("")){
			LoggerAspect.logWarn("获取风险翻译失败0！");
		}else{
			result = PCPostService.getHttpServiceJson(url, str);
			if(result == null){
				// DRD: 后续翻译失败，需返回前端请求失败
				ExceptionService.throwCodeException("----获取接口失败：getFXTransPCResultJson");
			}
		}
		return result;
	}
	
}
