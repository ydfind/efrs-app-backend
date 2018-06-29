package com.icbc.efrs.app.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.*;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * 构建APP请求对象
 * @author kfzx-dengrd
 *
 */
public class AppReqService {
	// 进行page、size等节点识别
	protected static void init(BaseAppReqEntity obj){
		JSONObject objJson = obj.getJsonObject();
		int page = 0;
		int size = 0;
		String key = "";
		String bankId = "";
		String userId = "";
		page = JSONUtil.getSubNodeInt(objJson, BaseAppReqEntity.NAME_PAGE, BaseAppReqEntity.DEF_PAGE);
		size = JSONUtil.getSubNodeInt(objJson, BaseAppReqEntity.NAME_SIZE, BaseAppReqEntity.DEF_SIZE);
		key = JSONUtil.getStringByKey(objJson, BaseAppReqEntity.NAME_KEY);
		bankId = JSONUtil.getStringByKey(objJson, BaseAppReqEntity.NAME_BANKID);
		userId = JSONUtil.getStringByKey(objJson, BaseAppReqEntity.NAME_USERID);
		switch(obj.getIntfType()){
		case FaHai:
			break;
		case FuzzyQuery:
			page = JSONUtil.getSubNodeInt(objJson, "reqParams.page", BaseAppReqEntity.DEF_PAGE);
			size = JSONUtil.getSubNodeInt(objJson, "reqParams.size", BaseAppReqEntity.DEF_SIZE);
			break;
		case FXWithParam:
		case FXWithParams:
			// 风险需自行分页
			objJson.remove(BaseAppReqEntity.NAME_PAGE);
			objJson.remove(BaseAppReqEntity.NAME_SIZE);
			// 风险需要读取key，然后构建："data": {"accNo": "中国工商银行32"}
			objJson.remove(BaseAppReqEntity.NAME_KEY);
			break;
		case CompanyQuery:
			break;
		case CompanyReport:
		case ZSWithParamNoPaged:
		case ZhongShu:
			break;
		default:// 
			ExceptionService.throwCodeException("无法识别此接口类型0");
		}	
		obj.setPage(page);
		obj.setSize(size);
		obj.setKey(key);
		obj.setBankId(bankId);
		obj.setUserId(userId);
	}
	// 工厂函数：根据type创建AppReqBaseEntity
	protected static BaseAppReqEntity getAppReqEntity(ReqIntfEnums type){
		BaseAppReqEntity obj = null;
		switch(type){
		case FaHai:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
			obj = new BaseAppReqEntity();
			break;
		case FuzzyQuery:
			obj = new BaseAppReqEntity();
			break;
		case FXWithParam:
		case FXWithParams:
			obj = new BaseAppReqEntity();
			break;
		case CompanyQuery:
			obj = new CompanyQueryAppReqEntity();
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型0 = " + type);
		}
		return obj;
	}
	// 解析字符串，解析失败返回null；
	public static BaseAppReqEntity parse(String body){
		BaseAppReqEntity obj = null;
		String serviceKey = "";
		ReqIntfEnums type = ReqIntfEnums.ErrIntf;
		try{
			JSONObject objJson = JSON.parseObject(body, Feature.OrderedField);
			// 取出服务的关键字,创建 app请求对象
			serviceKey = JSONUtil.getStringByKey(objJson, "serviceKey");
			objJson.remove("serviceKey");
			type = ReqJsonFilesProp.getReqIntfEnums(serviceKey);
			if(type == ReqIntfEnums.ErrIntf){
				ExceptionService.throwCodeException("找不到serviceKey = " + serviceKey + ";");
			}
			obj = getAppReqEntity(type);
			// 赋值 请求关键字、接口类型、json、页码、pagesize
			obj.setIntfType(type);
			obj.setJsonObject(objJson);
			obj.setServiceKey(serviceKey);
			init(obj);
//			objAppReq.setReqJsonFileEntity(reqJsonFile);
//			// 进行其它特殊的初始化
//			// 该节点不需要
		}catch(Exception e){
			e.printStackTrace();
			obj = null;
		}
		return obj;
	}
}
