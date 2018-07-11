package com.icbc.efrs.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.PCServerUrlsProp;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.prop.ServerProp;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * 根据App请求和配置文件，构建PC请求对象
 *
 */
public class ServerReqService {

	public static String getReqFormatTime(Date date){
		SimpleDateFormat df = new SimpleDateFormat(ServerProp.getGlobalDateFormatStr());
		return df.format(date);
	}
	
	/**
	 * 校正页码偏移量，默认App从1开始，但部分接口PC请求是从0开始的
	 * @param serverReq
	 * @param offset
	 * @param pagename
	 */
	private static boolean checkPageOffset(JSONObject data, int offset, String pagePath){
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
	
	private static boolean checkPageOffset(BaseServerReqEntity serverReq){
		boolean result = false;
		String serviceKey = serverReq.getServiceKey();
		ReqJsonFileEntity jsonFile = ReqJsonFilesProp.getJsonFileEntity(serverReq.getServiceKey());
		int offset = jsonFile.getAsReqPageOffset();
		if(offset != 0){
			String pagePath = jsonFile.getAsReqPageKey();
			result = checkPageOffset(serverReq.getJsonObject(), offset, pagePath);
		}
		return result;
	}

	private static void init(BaseServerReqEntity serverReq, BaseAppReqEntity appReq, boolean isComplex){
		// appReq、PcReqUrl、ReqTime、JsonObject
		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serverReq.getServiceKey());
		ReqIntfEnums type = serverReq.getReqIntf();
		serverReq.setPcReqUrl(PCServerUrlsProp.getPCReqUrl(type));
		serverReq.setReqTime(new Date());
		serverReq.setReqIntf(type);
		JSONObject objTarget = new JSONObject(true);
		serverReq.setJsonObject(objTarget);
		// 复制配置文件
		JSONUtil.copyJSONObject(objFile.getJsonObject(), objTarget);
		
		if(!isComplex){
			JSONUtil.copyJSONObject(appReq.getJsonObject(), objTarget);
		}else{
			// bankid、userid、page、size、key复制到请求报文里面去
			String key;
			key = Constants.REQ_NAME_BANKID;
			objTarget.put(key, appReq.getBankId());
			key = Constants.REQ_NAME_USERID;
			objTarget.put(key, appReq.getUserId());
			key = Constants.REQ_NAME_PAGE;
			if(objTarget.containsKey(key)){
				objTarget.put(key, String.valueOf(appReq.getPage()));
			}
			key = Constants.REQ_NAME_SIZE;
			if(objTarget.containsKey(key)){
				objTarget.put(key, String.valueOf(appReq.getSize()));
			}
			key = Constants.REQ_NAME_KEY;
			if(objTarget.containsKey(key)){
				objTarget.put(key, appReq.getKey());
			}
		}
		// 若配置文件存在time节点，则需要自动生成时间
		String key = "time";
		if(objFile.getJsonObject().containsKey(key)){
			serverReq.setReqTime(new Date());
			objTarget.put(key, serverReq.getReqTimeStr());
		}
	}
	
	private static void afterInit(BaseServerReqEntity serverReq, BaseAppReqEntity appReq, ReqIntfEnums parentType){
		ReqIntfEnums type = serverReq.getReqIntf();
		String serviceKey = serverReq.getServiceKey();
		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
		JSONObject objTarget = serverReq.getJsonObject();
		
		switch(type){
		case FaHai:;
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case PatentInfo:
		case ZSWithParam:
			break;
		case FXWithParam:
		case FXWithParams:
			// 风险需要读取key，然后构建："data": {"accNo": "中国工商银行32"}
			JSONObject fxData = new JSONObject(true);
			String fxReqId = objFile.getAsParam(0);
			String fxValue = appReq.getKey() + fxReqId;
			fxData.put("accNo", fxValue);
			objTarget.put("data", fxData);
			break;
		case AbnormalManage:
		case QualityCertification:
		case TeleFraud:
		case TaxIllegal:
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型2");
		}
		// 资质证书和违法税收中数接口：默认拿到全量数据，再进行分页
		if(parentType == ReqIntfEnums.QualityCertification ||
				(parentType == ReqIntfEnums.TaxIllegal && 
						type == ReqIntfEnums.ZSWithParam)){
			JSONUtil.copyJSONObjectField(objFile.getJsonObject(), 
					objTarget, Constants.REQ_NAME_PAGE);
			JSONUtil.copyJSONObjectField(objFile.getJsonObject(), 
					objTarget, Constants.REQ_NAME_SIZE);	
			// 先请求足够的数量，再进行分页		
//			int reqnum = appReq.getPage() * appReq.getSize();
//			String strPage = String.valueOf(BaseAppReqEntity.DEF_PAGE);
//			String strSize = String.valueOf(reqnum);
//			objTarget.put(BaseAppReqEntity.NAME_PAGE, strPage);
//			objTarget.put(BaseAppReqEntity.NAME_SIZE, strSize);
//			LoggerAspect.logInfo("设定查找page = " + strPage + "; size = " + strSize);
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
	private static BaseServerReqEntity createServerReqEntity(ReqIntfEnums type){
		BaseServerReqEntity objServerReq = null;
		switch(type){
		case FaHai:;
		case FuzzyQuery:
		case FXWithParam:
		case FXWithParams:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case PatentInfo:
		case ZSWithParam:
			objServerReq = new BaseServerReqEntity();
			break;
		case CompanyQuery:
			ExceptionService.throwCodeException("企业查询无法构建单一PC请求报文");
			break;
//		case AbnormalManage:
//		case QualityCertification:
//      case TeleFraud:
//		case TaxIllegal:
//			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
		}
		return objServerReq;
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
		
		target.put(Constants.REQ_NAME_BANKID, appReq.getBankId());
		target.put(Constants.REQ_NAME_USERID, appReq.getUserId());
		target.put("time", getReqFormatTime(new Date()));
		target.put("data", fengxianResult.getDatakeys());
			// 处理data：

		result = "[" + target.toString() + "]";
		return result;
	}
	 
	private static BaseServerReqEntity getServerReqEntity(BaseAppReqEntity appReq, 
			ReqIntfEnums type, String serviceKey, ReqIntfEnums parentType){
		BaseServerReqEntity serverReq = null;
		if(serviceKey == null || serviceKey.equals("")){
			ExceptionService.throwCodeException("serviceKey为null或空，无发封装成pc请求报文  = " + serviceKey);
			return serverReq;
		}
		if(type == ReqIntfEnums.ErrIntf || type == ReqIntfEnums.CompanyQuery || 
				type == ReqIntfEnums.AbnormalManage || type == ReqIntfEnums.QualityCertification
				|| type == ReqIntfEnums.TeleFraud){
			ExceptionService.throwCodeException("复杂查询、错误类型无法进行封装成pc请求报文  = " + type);
			return serverReq;
		}
		serverReq = createServerReqEntity(type);
		serverReq.setServiceKey(serviceKey);
		serverReq.setReqIntf(type);
		boolean isComplex = parentType != ReqIntfEnums.ErrIntf;
		init(serverReq, appReq, isComplex);
		afterInit(serverReq, appReq, parentType);
		return serverReq;
	}
	
	/**
	 * 根据请求，构建PC请求的post请求对象：法海等请求
	 * 
	 * @param appReq App请求对象
	 * @return APP Server请求对象
	 */
	public static BaseServerReqEntity getServerReqEntity(BaseAppReqEntity appReq){
		ReqIntfEnums type = appReq.getIntfType();	
		String serviceKey = appReq.getServiceKey();
		return getServerReqEntity(appReq, type, serviceKey, ReqIntfEnums.ErrIntf);
	}
	
	/**
	 * 根据请求，构建PC请求的post请求对象：资质证书等
	 * 
	 * @param servicekey APP请求的约定关键字
	 * @param appReq App请求对象（包含请求的一些参数）
	 * @return APP Server请求对象
	 */
	private static BaseServerReqEntity getServerReqEntity(BaseAppReqEntity appReq, String serviceKey){
		ReqIntfEnums type = ReqIntfEnums.ErrIntf;
		// 校验是否应该进行
		ReqJsonFileEntity jsonFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
		if(jsonFile == null){
			ExceptionService.throwCodeException(serviceKey + ":无法进行封装成pc请求报文");
		}else{
			type = jsonFile.getReqType();// serviceKey决定pc post的类型
		}
		return getServerReqEntity(appReq, type, serviceKey, appReq.getIntfType());
	}

	/**
	 * 目前企业查询使用\经营异常\资质认证
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
				serverReq = getServerReqEntity(appReq, serviceKey);
			}
			serverReqs.add(serverReq);
		}
		return serverReqs;
	}
}
