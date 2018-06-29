package com.icbc.efrs.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.PCServerUrlsProp;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.prop.ServerProp;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * 构建APP服务器请求对象
 * @author kfzx-dengrd
 *
 */
public class ServerReqService {
	/**
	 * 校正页码偏移量，默认App从1开始，但部分接口PC请求是从0开始的
	 * @param serverReq
	 * @param offset
	 * @param pagename
	 */
	public static boolean checkPageOffset(JSONObject data, int offset, String pagePath){
		boolean result = false;
		if(offset == 0 || data != null){
			String[] keys = pagePath.split("\\.");
			JSONObject obj = JSONUtil.searchKeyParent(data, keys);
			if(obj != null){
				String key = keys[keys.length - 1];
				Object page = obj.get(key);
				try{
					if(page instanceof String){
						page = String.valueOf(Integer.parseInt((String)page) + offset);
					}else if(page instanceof Integer){
						page = Integer.valueOf((Integer)page + offset);
					}else{
						ExceptionService.throwCodeException("无法识别page节点的这个类型；key = " + key + ";" + page.toString());
					}
					obj.put(key, page);
					result = true;
				}catch(Exception e){
					ExceptionService.throwCodeException("无法校正page节点值，请查看配置文件是否正确; key = " + key + ";" + page.toString());
				}
			}
		}
		if(!result){
			ExceptionService.throwCodeException("page校验失败！");
		}
		return result;
	}
	
	public static boolean checkPageOffset(BaseServerReqEntity serverReq){
		boolean result = false;
		String serviceKey = serverReq.getServiceKey();
		ReqJsonFileEntity jsonFile = ReqJsonFilesProp.getJsonFileEntity(serverReq.getServiceKey());
		int offset = jsonFile.getAsPageOffset();
		if(offset != 0){
			String pagePath = jsonFile.getAsPagePath();
			result = checkPageOffset(serverReq.getJsonObject(), offset, pagePath);
		}
		return result;
	}
	
	public static String getReqFormatTime(Date date){
		SimpleDateFormat df = new SimpleDateFormat(ServerProp.getGlobalDateFormatStr());
		return df.format(date);
	}
	
	protected static void init(BaseServerReqEntity serverReq, BaseAppReqEntity appReq){
		// appReq、PcReqUrl、ReqTime、JsonObject
		ReqIntfEnums reqType = serverReq.getReqIntf();
//		serverReq.setAppReq(appReq);
		serverReq.setPcReqUrl(PCServerUrlsProp.getPCReqUrl(reqType));
		serverReq.setReqTime(new Date());
		JSONObject objJson = new JSONObject(true);
		serverReq.setJsonObject(objJson);
		JSONUtil.copyJSONObject(
				ReqJsonFilesProp.getJsonFileEntity(serverReq.getServiceKey()).getJsonObject(), 
				objJson);
		JSONUtil.copyJSONObject(appReq.getJsonObject(), objJson);
		afterInit(appReq.getIntfType(), appReq, serverReq);
	}
	
	protected static void afterInit(ReqIntfEnums type, BaseAppReqEntity appReq, BaseServerReqEntity serverReq){
		String serviceKey = serverReq.getServiceKey();
		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
		JSONObject objTarget = serverReq.getJsonObject();
		// 若配置文件存在time节点，则需要自动生成时间
		String key = "time";
		if(objFile.getJsonObject().containsKey(key)){
			serverReq.setReqTime(new Date());
			objTarget.put(key, serverReq.getReqTimeStr());
		}
		switch(type){
		case FaHai:;
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
			break;
		case FXWithParam:
		case FXWithParams:
			// 风险需要读取key，然后构建："data": {"accNo": "中国工商银行32"}
			JSONObject fxData = new JSONObject(true);
			String fxReqId = ReqJsonFilesProp.getJsonFileEntity(serverReq.getServiceKey()).getAsParam(0);
			String fxValue = appReq.getKey() + fxReqId;
			fxData.put("accNo", fxValue);
			objTarget.put("data", fxData);
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型2");
		}
		// 处理页码偏移问题
		checkPageOffset(serverReq);
	}
	
	/**
	 * 工厂函数：根据接口类型，创建server请求对象
	 * 
	 * @param servicekey APP请求的约定关键字
	 * @param type App请求的接口类型
	 * @return APP Server请求对象
	 */
	protected static BaseServerReqEntity getServerReqEntity(ReqIntfEnums type){
		BaseServerReqEntity objServerReq = null;
		switch(type){
		case FaHai:;
		case FuzzyQuery:
		case FXWithParam:
		case FXWithParams:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
			objServerReq = new BaseServerReqEntity();
			break;
		case CompanyQuery:
			ExceptionService.throwCodeException("企业查询无法构建单一PC请求报文");
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
		}
		return objServerReq;
	}
	
	/**
	 * 根据请求，构建PC请求的post请求对象
	 * 
	 * @param appReq App请求对象
	 * @return APP Server请求对象
	 */
	public static BaseServerReqEntity getServerReqEntity(BaseAppReqEntity appReq){
		BaseServerReqEntity serverReq = null;
		ReqIntfEnums reqType = appReq.getIntfType();
		serverReq = getServerReqEntity(reqType);
		serverReq.setServiceKey(appReq.getServiceKey());
		serverReq.setReqIntf(appReq.getIntfType());
		init(serverReq, appReq);
		return serverReq;
	}
	/**
	 * 根据请求，构建PC请求的post请求对象
	 * 
	 * @param appResult PC端返回结果对象（需要翻译的对象）
	 * @param appReq App请求对象（包含请求的一些参数）
	 * @param content 请求模板
	 * @return 风险翻译APP Server请求对象
	 */
	public static String parseFXTransPost(BaseAppResultEntity appResult, BaseAppReqEntity appReq, String content){
		if(!(appResult instanceof FengXianAppResultEntity)){
			ExceptionService.throwCodeException("非风险接口不能得到风险翻译数据！");
			return "";
		}
		
		FengXianAppResultEntity fengxianResult = (FengXianAppResultEntity)appResult;
		String result = "";
		JSONObject target = JSON.parseObject(content);
		// 对content进行处理
		
		target.put(BaseAppReqEntity.NAME_BANKID, appReq.getBankId());
		target.put(BaseAppReqEntity.NAME_USERID, appReq.getUserId());
		target.put("time", getReqFormatTime(new Date()));
		target.put("data", fengxianResult.getDatakeys());
			// 处理data：

		result = "[" + target.toString() + "]";
		return result;
	}
	 
	/**
	 * 初始化企业查询相关的请求
	 * 
	 */
	protected static void initCompanyQueryReq(ReqJsonFileEntity jsonFile, BaseAppReqEntity appReq, BaseServerReqEntity serverReq){
		//ReqIntfEnums reqType = appReq.getIntfType();
		ReqIntfEnums type = jsonFile.getReqType();
//		serverReq.setAppReq(null);// 此时无法访问appReq
		serverReq.setPcReqUrl(PCServerUrlsProp.getPCReqUrl(type));
		serverReq.setReqTime(new Date());
		JSONObject objJson = new JSONObject(true);
		serverReq.setJsonObject(objJson);
		
		// 复制XXXappserver.json内容到objJson
		JSONUtil.copyJSONObject(jsonFile.getJsonObject(), objJson);
		String key;
		key = BaseAppReqEntity.NAME_BANKID;
		objJson.put(key, appReq.getBankId());
		key = BaseAppReqEntity.NAME_USERID;
		objJson.put(key, appReq.getUserId());
		key = BaseAppReqEntity.NAME_PAGE;
		if(objJson.containsKey(key)){
			objJson.put(key, String.valueOf(appReq.getPage()));
		}
		key = BaseAppReqEntity.NAME_SIZE;
		if(objJson.containsKey(key)){
			objJson.put(key, String.valueOf(appReq.getSize()));
		}
		key = BaseAppReqEntity.NAME_KEY;
		if(objJson.containsKey(key)){
			objJson.put(key, appReq.getKey());
		}
		afterInit(type, appReq, serverReq);
	}
	
	/**
	 * 根据请求，构建PC请求的post请求对象
	 * 
	 * @param servicekey APP请求的约定关键字
	 * @param appReq App请求对象（包含请求的一些参数）
	 * @return APP Server请求对象
	 */
	public static BaseServerReqEntity getServerReqEntity(String serviceKey, BaseAppReqEntity appReq){
		BaseServerReqEntity serverReq = null;
		ReqIntfEnums type = ReqIntfEnums.ErrIntf;
		// 校验是否应该进行
		ReqJsonFileEntity jsonFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
		if(jsonFile == null){
			ExceptionService.throwCodeException(serviceKey + ":无法进行封装成pc请求报文");
			return serverReq;
		}
		type = jsonFile.getReqType();// serviceKey决定pc post的类型
		if(type == ReqIntfEnums.CompanyQuery || type == ReqIntfEnums.ErrIntf || 
				type == ReqIntfEnums.FuzzyQuery){
			ExceptionService.throwCodeException("企业查询、首页模糊查询、错误类型无法进行封装成pc请求报文");
			return serverReq;
		}
		// 
	    serverReq = getServerReqEntity(type);
		serverReq.setServiceKey(serviceKey);
	    initCompanyQueryReq(jsonFile, appReq, serverReq);
		return serverReq;
	}

	/**
	 * 目前企业查询使用
	 * @return 返回服务器端的请求报文集合
	 */
	public static ArrayList<BaseServerReqEntity> getServerReqs(ArrayList<String> serviceKeys, BaseAppReqEntity appReq){
		if(serviceKeys == null || appReq == null){
			ExceptionService.throwCodeException("无法请求对象为null或serviceKeys为null的情况");
			return null;
		}
		ArrayList<BaseServerReqEntity> serverReqs = new ArrayList<BaseServerReqEntity>();
		for(int i = 0; i < serviceKeys.size(); i++){
			String serviceKey = serviceKeys.get(i);
			BaseServerReqEntity serverReq = null;
			if(serviceKey != null && !serviceKey.equals("")){
				serverReq = ServerReqService.getServerReqEntity(serviceKey, appReq);
			}
			serverReqs.add(serverReq);
		}
		return serverReqs;
	}
}
