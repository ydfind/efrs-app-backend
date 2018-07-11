package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.domain.BaseAppReqEntity;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.ComplexAppResultEntity;
import com.icbc.efrs.app.domain.FengXianAppResultEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * 根据PC端返回结果，构建APP的返回结果对象
 *
 */
public class AppResultService {
	
	public static BaseAppResultEntity getErrAppResultEntity(String msg){
		BaseAppResultEntity appResult = new BaseAppResultEntity();
		appResult.setJsonTarget(new JSONObject(true));
		setResultError(appResult, null, msg);	
		return appResult;
	}
	
	public static ComplexAppResultEntity getErrComplexResultEntity(String msg){
		ComplexAppResultEntity appResult = new ComplexAppResultEntity();
		appResult.setJsonTarget(new JSONObject(true));
		setResultError(appResult, null, msg);	
		return appResult;
	}
	// 工厂：根据type创建，app请求返回对象
	public static BaseAppResultEntity getAppResultEntity(ReqIntfEnums type){
		BaseAppResultEntity appResult = null;
		switch(type){
		case FaHai:;
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case ZSWithParam:
		case PatentInfo:
			appResult = new BaseAppResultEntity();
			break;
		case FXWithParam:
		case FXWithParams:
			appResult = new FengXianAppResultEntity();
			break;
		case CompanyQuery:
			ExceptionService.throwCodeException("无法识别此接口类型CompanyQuery");
			break;
		case AbnormalManage:
		case QualityCertification:
		case TeleFraud:
		case TaxIllegal:
			appResult = new ComplexAppResultEntity();
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型getAppResultEntity = " + type);
		}
		return appResult;
	}
	
	// 返回code的key
	private static String getCodeKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "respCd";
			break;
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case PatentInfo:
			result = "CODE";
			break;
		case ZSWithParam:// 软件著作、作品著作、税收违法中数接口
			result = "code";
			break;
		case FXWithParam:
		case FXWithParams:
			result = "respCd";
			break;
//		case AbnormalManage:
//		case QualityCertification:
//      case TeleFraud:
//		case TaxIllegal:
		// 已在配置文件配置，不会跑到这里
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的成功码");
		}
		return result;
	}
	
	// 返回msg的key
	private static String getMsgKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
		case FXWithParam:
		case FXWithParams:
			result = "msg";
			break;
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
		case PatentInfo:
			result = "MSG";
			break;
		case ZSWithParam:// 软件和作品著作权没有返回msg；
			result = "MSG";
			break;
//			case AbnormalManage:
//		case QualityCertification:
//      case TeleFraud:
//		case TaxIllegal:
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的msg");
		}
		return result;
	}
	
	// 返回data的key
	private static String getDataKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "result";
			break;
		case FuzzyQuery:
			result = "";// 特殊处理
			break;
		case FXWithParam:
		case FXWithParams:
			result = "data";
			break;
		case ZhongShu:
			result = "DATA";
			break;
		case CompanyReport:
			result = "ENT_INFO";
			break;
//			case AbnormalManage:
//		case QualityCertification:
//		case TeleFraud:
//		case TaxIllegal:
//		case ZSWithParam:// 目前仅软件著作、作品著作，在配置文件配置，无法走到这里
//		case PatentCertification:
//		case ZSWithParamNoPaged:
//			result = "ENT_INFO.FRPOSITION";// 在配置文件配置,无法走到这里
//			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的msg = " + type);
		}
		return result;
	}
	
	// 返回totalnum的key
	private static String getTotalNumKey(ReqIntfEnums type){
		String result = "";
		switch(type){
		case FaHai:
			result = "totalNum";
			break;
		case FuzzyQuery:
			result = "TOTALCOUNT";
			break;
		case FXWithParam:
		case FXWithParams:
		case CompanyReport:
		case ZSWithParamNoPaged:
			result = "";// 需要自行计算出总数
			break;
		case ZhongShu:
			result = "TOTALCOUNT";
			break;
//			case AbnormalManage:
//		case QualityCertification:
//      case TeleFraud:
//		case TaxIllegal:
//		case PatentCertification:
//		case ZSWithParam:
//			break;// 目前在配置文件配置，不会跑到这里
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的总数量key" + type);
		}
		return result;
	}
	
	// 识别总数、data节点、成功码、msg
	private static void init(BaseAppResultEntity appResult, ReqIntfEnums type, String serviceKey){
		JSONObject jsonSource = appResult.getJsonSource();
		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
//		JSONObject jsonTarget = appResult.getJsonTarget();
		String key;
		// msg
		key = getMsgKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			appResult.setMsg(jsonSource.getString(key));
		}else if(type == ReqIntfEnums.ZSWithParam){// 中数2004目前软件著作、作品著作没有msg
			LoggerAspect.logWarn("----------中数2004目前软件著作、作品著作pc端返回的结果没有msg");
		}
		else{
			appResult.setMsg("PC服务器错误：找不到后端返回的msg节点");
		}
		String nodeKey;
		Object nodeValue;
		// code节点
		String code = "";
		if(objFile != null && (objFile.getAsResCodeKey().length() > 0)){
			nodeKey = objFile.getAsResCodeKey();
			code = JSONUtil.getSubNodeStr(jsonSource, nodeKey);
			if(code == null || code.equals("")){
				LoggerAspect.logError("无法识别配置文件指定的code，或code为空， = " + nodeKey);
			}
		}else{
			key = getCodeKey(type);
			if((key.length() > 0) && jsonSource.containsKey(key)){
				appResult.setCode(jsonSource.getString(key));
			}else{
				appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
				appResult.setMsg("PC服务器错误：找不到后端返回的成功码节点");
			}
		}
		// totalNum
		int totalnum = 0;
		if(objFile != null && (objFile.getAsResTotalnumKey().length() > 0)){
			nodeKey = objFile.getAsResTotalnumKey();
			totalnum = JSONUtil.getSubNodeInt(jsonSource, nodeKey, totalnum);
		}else if(serviceKey.equals(Constants.REQ_NAME_COMPLEX_SSWF_ZS)){
			LoggerAspect.logWarn("税收违法ZS无法找到总数");
		}else{ 
			key = getTotalNumKey(type);
			if((key.length() > 0) && jsonSource.containsKey(key)){
				totalnum = JSONUtil.getIntegerByKey(jsonSource, key, totalnum);
			}// 企业年报没有totalnum、税收违法ZS 目前无page和size
		}
		appResult.setTotalnum(totalnum);
		// Data节点识别
		if(objFile != null && (objFile.getAsResDataKeys().size() == 1)){
			nodeKey = objFile.getAsResDataKeys().get(0);
			nodeValue = JSONUtil.getSubNodeObj(jsonSource, nodeKey);
			if(nodeValue != null){
				appResult.setJsonSData(nodeValue);
				appResult.setJsonTData(nodeValue);
			}else{
				//appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
				appResult.setJsonSData(null);
				appResult.setJsonTData(null);
				if(type != ReqIntfEnums.FuzzyQuery){
					ExceptionService.throwCodeException("无法从后台服务器返回报文中找到data节点, = " + nodeKey);
				}
			}
		}else{
			key = getDataKey(type);
			if((key.length() > 0) && jsonSource.containsKey(key)){
				appResult.setJsonSData(jsonSource.get(key));
				appResult.setJsonTData(jsonSource.get(key));
			}else{
				//appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
				appResult.setJsonSData(null);
				appResult.setJsonTData(null);
				if(type != ReqIntfEnums.FuzzyQuery &&
						!serviceKey.equals(Constants.REQ_NAME_COMPLEX_SSWF_ZS) &&// 复杂请求是可能请求不到数据的
						!serviceKey.equals(Constants.REQ_NAME_COMPLEX_SSWF_FX)){
					ExceptionService.throwCodeException("无法从后台服务器返回报文中找到data节点 = " + type);
				}
			}
		}
	}

	/** 
	 * 复杂请求业务的返回结果中data节点构造
	 * @param appResult
	 * @param type
	 * @param serviceKeys
	 */
	private static void buildData(ComplexAppResultEntity appResult, ReqIntfEnums type, 
			ArrayList<String> serviceKeys){
		switch(type){
//		case FaHai:
//		case ZhongShu:
//		case ZSWithParam:
//		case FuzzyQuery:
//		case FXWithParam:
//		case FXWithParams:
//		case CompanyReport:
//		case PatentInfo:
//		case ZSWithParamNoPaged:
		case QualityCertification:
		case AbnormalManage:
		case TeleFraud:
		case TaxIllegal:
			try{
				JSONArray jsonData = new JSONArray();
				appResult.setJsonTData(jsonData);
				for(int i = 0; i < serviceKeys.size(); i++){
					BaseAppResultEntity subResult = appResult.getSubResults().get(i);
					Object objData = subResult.getJsonTData();
					if(objData == null){
						LoggerAspect.logWarn("复杂请求 " + type + " 的第" + i + "个请求data为null");
					}
					else if(objData instanceof JSONArray){
						JSONUtil.copyJSONArray((JSONArray)objData, jsonData);
					}else if(objData instanceof JSONObject){
						ExceptionService.throwCodeException("不应该为JSONObject类型 " + objData.toString() + 
								" - " + serviceKeys.get(i));
					}
					else{
						// DRD NOTE: 税收违法中数result.detail可能不是数组，为String的“无数据”！
						if(!serviceKeys.get(i).equals(Constants.REQ_NAME_COMPLEX_SSWF_ZS)){
							ExceptionService.throwCodeException("未能识别该类型 " + objData.getClass() + 
									" - " + serviceKeys.get(i));
						}
					}
				}
			}catch(Exception e){
				ExceptionService.throwCodeException("处理复杂请求对象报错");
			}
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型buildData = " + type);
		}	
	}
	
	private static int calcTotalnum(ComplexAppResultEntity appResult){
		int totalnum = 0;
		for(int i = 0; i < appResult.getSubResults().size(); i++){
			BaseAppResultEntity subResult = appResult.getSubResults().get(i);
			if(!subResult.isPcReqOK()){
				continue;// 未请求成功的就不计算总数了
			}
			Object objData = subResult.getJsonTData();
			if(objData == null){
				LoggerAspect.logWarn("复杂请求  的第" + i + "个请求data为null");
			}
			else if(objData instanceof JSONArray){
				// 总数量
				totalnum += subResult.getTotalnum();
			}else if(objData instanceof JSONObject){
				ExceptionService.throwCodeException("未能识别该类型JSONObject");
			}else if(objData instanceof String){
				LoggerAspect.logError("data的value类型为String，目前在税收违法的中数接口是正确的");
			}else{
				ExceptionService.throwCodeException("未能识别该类型" + objData.getClass());	
			}
		}
		return totalnum;
	}
	
	private static void init(ComplexAppResultEntity appResult){
	    appResult.setCode("-1");// 默认报错，后续需要处理
	    appResult.setMsg("复杂请求默认初始化值");
	    appResult.setTotalnum(0);
	}
	// 进行json的组装
	private static void afterInit(BaseAppResultEntity appResult, ReqIntfEnums type, String serviceKey){
//		int listtype = 0;//1----企业年报查询;2----动产抵押查询;3----风险有数据，无明细；4----风险无数据
		// 特殊业务
		if(serviceKey.equals(Constants.REQ_NAME_COMPLEX_SSWF_ZS)){
			int totalnum = 0;
			Object obj = appResult.getJsonSData();
			if(obj != null){
				if(obj instanceof JSONArray){
					totalnum = ((JSONArray)obj).size();
				}
			}
			appResult.setTotalnum(totalnum);
		}
		switch(type){
		case FaHai:
		case ZhongShu:
		case ZSWithParam:
//		case AbnormalManage:
//		case QualityCertification:
//		case TeleFraud:
//		case TaxIllegal:
			break;
		case FuzzyQuery:
			initCompanyQueryParam(appResult);
			break;
		case FXWithParam:
		case FXWithParams:
			initFXWithParam(appResult, serviceKey);
			break;
		case CompanyReport:
			initCompanyReportParam(appResult);
			break;
		case PatentInfo:
			initPatentCertificationParam(appResult);
			break;
		case ZSWithParamNoPaged:// 计算totalnum
			Object objTData = appResult.getJsonTData();
			if(objTData == null){
				ExceptionService.throwCodeException("目标data不能为null");
			}
			int totalnum = appResult.getTotalnum();
			if(objTData instanceof JSONArray){
				JSONArray jsonArray = (JSONArray)(objTData);
				totalnum = jsonArray.size();
			}else if(objTData instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)(objTData);
				// 企业照面信息为JSONObject类型，存在数据时，totalnum为1，否则为0
				if(serviceKey.equals(Constants.REQ_NAME_ZS_ZMXX) && jsonObject.size() > 0){
					totalnum = 1;
				}
			}
			appResult.setTotalnum(totalnum);
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型AppResultService.afterInit = " + type);
		}
	}
	
	private static void afterInit(ComplexAppResultEntity appResult, ReqIntfEnums type, 
			ArrayList<String> serviceKey){
		buildData(appResult, type, serviceKey);
		switch(type){
//		case FaHai:
//		case ZhongShu:
//		case ZSWithParam:
//		case FuzzyQuery:
//		case FXWithParam:
//		case FXWithParams:
//		case CompanyReport:
//		case PatentInfo:
//		case ZSWithParamNoPaged:
		case AbnormalManage:
		case QualityCertification:
		case TeleFraud:
		case TaxIllegal:
			// 计算总页数
			appResult.setTotalnum(calcTotalnum(appResult));
			appResult.setCode("0000");
			appResult.setMsg("请求成功");
			// 设置listype
			if(type == ReqIntfEnums.TeleFraud){
				int listtype = 4;
				for(int i = 0; i < appResult.getSubResults().size(); i++){
					BaseAppResultEntity subResult = appResult.getSubResults().get(i);
					if(subResult != null){
						int subListtype = subResult.getListtype();
						if(subListtype < listtype){
							listtype = subListtype;
						}
					}
				}
				appResult.setListtype(listtype);
			}
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型afterInit = " + type);
		}
	}
	
	// 风险带参数3004处理函数
	private static void initFXWithParam(BaseAppResultEntity appResult, String serviceKey){
		// 1）取出参数，处理json到data节点；2）自行计算总数量、总页数、listtype；3）分页独立到新的服务里面；
		ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
		ArrayList<String> endStrs = objFile.getAsFXParams();
		// 校验，看以前的是否已经清除干净
		if(objFile.getAsParams().size() > 1){
			ExceptionService.throwCodeException("未清除干净：" + serviceKey);
		}
		ArrayList<String> paramkeys = new ArrayList<String>();// 翻译相关
		int listtype = 4;
		int totalnum = 0;
		if(appResult.getJsonSData() instanceof JSONObject){
			JSONObject oldData = (JSONObject)appResult.getJsonSData();
			JSONArray newData = new JSONArray();
		    Set<String> keys = oldData.keySet();
		    // 把所有后缀为param的acclist下节点拷贝到newData中；
		    for(String key: keys){
		    	if(getStrEndIn(key, endStrs)){
		    		paramkeys.add(key);
		    		if(listtype == 4)
		    			listtype = 3;
		    		Object objAcc = JSONUtil.getSubNodeObj(oldData, key + ".accList");
		    		if(objAcc == null){
		    			LoggerAspect.logWarn("无法找到风险数据accList节点");
		    		}
		    		else if(objAcc instanceof JSONArray){
		    			if(listtype == 3)// 有数据、有明细
		    				listtype = 0;
		    			JSONUtil.copyJSONArray((JSONArray)objAcc, newData);
		    		}
		    		else{
		    			ExceptionService.throwCodeException("风险找到的aclist节点不是JSONArray类型" + objAcc.getClass());
		    		}
		    	}
		    }
		    totalnum = newData.size();
		    appResult.setJsonTData(newData);
		}
		else{
			LoggerAspect.logError("无法找到风险带参数接口，从PC端返回的data节点");
		}
		appResult.setListtype(listtype);
		appResult.setTotalnum(totalnum);
		String datakeys = "";
		if(paramkeys.size() > 0){
			datakeys = paramkeys.get(0);
			for(int i = 1; i < paramkeys.size(); i++){
				datakeys += "|" + paramkeys.get(i);
			}
		}
		((FengXianAppResultEntity)appResult).setDatakeys(datakeys);
	}
	
	private static boolean getStrEndIn(String str, ArrayList<String> ends){
		for(int i = 0; i < ends.size(); i++){
			if(str.endsWith(ends.get(i))){
				return true;
			}
		}
		return false;
	}
	
	/* 首页模糊查询，不需要data节点，需要AREACODECOUNT、ENTERPRISES、INDUSTRYPHYCOUNT */
	private static void initCompanyQueryParam(BaseAppResultEntity appResult){
		JSONObject jsonSource = appResult.getJsonSource();
		String key;
		JSONObject jsonTData = new JSONObject(true);
		appResult.setJsonTData(jsonTData);
		if(appResult.getJsonSData() == null){
			appResult.setJsonSData(jsonTData);
		}
		// 首页模糊查询比较特殊----已改为放到data节点下
		JSONObject obj = jsonTData;
		key = "AREACODECOUNT";
		if(jsonSource.containsKey(key)){
			obj.put(key, jsonSource.get(key));
		}else{
			obj.put(key, new JSONArray());
			ExceptionService.throwCodeException("无法找到节点：" + key);
		}
		
		key = "ENTERPRISES";
		if(jsonSource.containsKey(key)){
			obj.put(key, jsonSource.get(key));
		}else{
			obj.put(key, new JSONArray());
			ExceptionService.throwCodeException("无法找到节点：" + key);
		}

		key = "INDUSTRYPHYCOUNT";
		if(jsonSource.containsKey(key)){
			obj.put(key, jsonSource.get(key));
		}else{
			obj.put(key, new JSONArray());
			ExceptionService.throwCodeException("无法找到节点：" + key);
		}
	}
	/* 企业年报*/
	private static void initCompanyReportParam(BaseAppResultEntity appResult){
		// listtype = 1；
		appResult.setListtype(1);
		// 构造data；data里按年份由近往后排序，
		Object obj = appResult.getJsonSData();
		JSONObject jsonTData = new JSONObject(true);
		// 默认结果为空
		appResult.setJsonTData(jsonTData);
		if(!(obj instanceof JSONObject)){
			ExceptionService.throwCodeException("企业年报的data应该为jsonobject类型");
			return;
		}
		JSONObject jsonSData = (JSONObject)obj;
		String keyBasic = "YEARREPORTBASIC";
		String keyYear = "ANCHEYEAR";
		String keyYearId = "ANCHEID";
		JSONArray jsonSBase = jsonSData.getJSONArray(keyBasic);
		// 若该节点为空，则无法构造企业年报
		if(jsonSBase == null || jsonSBase.size() < 1){
			ExceptionService.throwCodeException("企业年报的YEARREPORTBASIC节点为空，无法构造企业年报");
			return;
		}
		// 查找所有year节点
		Map<String, String> yearMap = new LinkedHashMap<String, String>();
		for(int i = 0; i < jsonSBase.size(); i++){
			JSONObject jsonItem = jsonSBase.getJSONObject(i);
			String year = jsonItem.getString(keyYear);
			String ancheid = jsonItem.getString(keyYearId);
			if(year == null || ancheid == null){
				ExceptionService.throwCodeException("企业年报的YEARREPORTBASIC中某节点ANCHEYEAR(或ANCHEID)为空或不存在，无法构造该年份 ");
				continue;
			}	
			if(yearMap.containsKey(ancheid)){
				ExceptionService.throwCodeException("该年份对应key以存在！");
			}
			yearMap.put(ancheid, year);
		}
		// 排序、构建data下的所有year节点
		ArrayList<Map.Entry<String, String>> list = 
			new ArrayList<Map.Entry<String, String>>(yearMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, String>>(){

			@Override
			public int compare(Entry<String, String> arg0,
					Entry<String, String> arg1) {
				// TODO Auto-generated method stub
				int leftVal = Integer.parseInt(arg0.getValue()); 
				int rightVal = Integer.parseInt(arg1.getValue());
				return rightVal - leftVal;
			}
		});
		for(Map.Entry<String, String> entry: list){
			addCompanyReportYear(jsonTData, entry.getValue());
			LoggerAspect.logInfo("---------查找到的年份为：" + entry.getKey() + "; 及value为：" + entry.getValue());
		}
		LoggerAspect.logInfo("------最终结果为：" + jsonTData.toString());
		// 遍历jsonSData下除BASIC外的节点，复制到jsonTData相应年份下
		Set<String> keys = jsonSData.keySet();
		for(String key: keys){
			if(!key.startsWith("YEAR")){
				continue;// 跳过企业年报不需要的节点
			}
			JSONArray jsonArray = jsonSData.getJSONArray(key);
			if(jsonArray == null){
				ExceptionService.throwCodeException(key + " 节点不是array类型，无法解析！");
				continue;
			}
			// 在每个year下创建该key节点
			addCompanyReportYearSubNode(jsonTData, key);// 所有月份都需要添加该节点
			// 遍历array中每一项，添加到相应年份中
			for(int j = 0; j < jsonArray.size(); j++){
				JSONObject arrayItem = jsonArray.getJSONObject(j);
				String ancheid = arrayItem.getString(keyYearId);

				if(ancheid == null || !yearMap.containsKey(ancheid)){
					ExceptionService.throwCodeException("企业年报的" + key + "中第" + j + 
							"节点ANCHEID(或在basic中无对应关系)为空或不存在！ ");
					continue;
				}
				String year = yearMap.get(ancheid);
				JSONObject yearObject = jsonTData.getJSONObject(year);// 得到年份节点
				JSONArray yearSub = null;
				// 前面必然已经创建过该年份下的子节点
				yearSub = yearObject.getJSONArray(key);
				if(yearSub == null){
					ExceptionService.throwCodeException("企业年报的" + key + "节点不为Array类型!");
					continue;
				}
				// 
				yearSub.add(arrayItem);
			}
		}
		// 处理totalnm等
		appResult.setTotalnum(yearMap.size());
	}
	
	/* 专利认证*/
	private static void initPatentCertificationParam(BaseAppResultEntity appResult){
		// listtype = 1；
		appResult.setListtype(2);
		// 把data节点里面的"BASIC"节点由JSONObject改为JSONArray类型；
		String nodeBasic = "BASIC";
		Object obj = appResult.getJsonSData();
		if(obj != null && obj instanceof JSONArray){
			JSONArray objData = (JSONArray)obj;
		    for(int i = 0; i < objData.size(); i++){
		    	Object objItem = objData.get(i);
		    	if(objItem instanceof JSONObject){
		    		JSONObject jsonObject = (JSONObject)objItem;
		    		if(jsonObject.containsKey(nodeBasic)){
		    			JSONArray item = new JSONArray();
		    			item.add(jsonObject.get(nodeBasic));
		    			jsonObject.put(nodeBasic, item);
		    		}else{
		    			ExceptionService.throwCodeException("专利信息找不到节点" + nodeBasic);
		    		}
		    	}else{
					ExceptionService.throwCodeException("节点不是JSONObject类型");
		    	}
		    }
		}else{
			ExceptionService.throwCodeException("找不到专利信息的data节点，或专利信息的节点不是jsonarray类型");
		}
		appResult.setJsonTData(obj);
	}
	
	private static void addCompanyReportYear(JSONObject jsonTData, String year){
		JSONObject obj = new JSONObject(true);
		jsonTData.put(year, obj);
	}
	
	private static void addCompanyReportYearSubNode(JSONObject jsonTData, String subName){
		for(String key: jsonTData.keySet()){
			JSONObject yearObject = jsonTData.getJSONObject(key);// 得到年份节点
			JSONArray yearSub = new JSONArray();
			if(yearObject.containsKey(subName)){
				ExceptionService.throwCodeException(key + "年份下已存在节点" + subName);
				
			}
			yearObject.put(subName, yearSub);
		}
	}
	
	// 产生app请求返回对象中的json报文
	public static void buildAppResultJson(BaseAppResultEntity appResult, BaseAppReqEntity appReq){
		if(appResult == null){
			ExceptionService.throwCodeException("无法初始化null的请求结果");
			return;
		}
		try{
			buildAppResultJson(appResult);
		}catch(Exception e){
			ExceptionService.throwCodeException("请求结果构造失败0");
		}
		try{
			if(appReq != null){
				Double pagesize = 10.0;
				if(appReq != null)
				    pagesize = appReq.getSize() * 1.0;// 以使计算结果能四舍五入
				int totalpage = 0;
				// 通常业务
				JSONObject jsonTarget = appResult.getJsonTarget();
				totalpage = (int)Math.ceil(appResult.getTotalnum() / pagesize);
				jsonTarget.put("totalpage", String.valueOf(totalpage));
			}
		}catch(Exception e){
			ExceptionService.throwCodeException("请求结果构造失败1");
		}
	}
	
	private static void buildAppResultJson(BaseAppResultEntity appResult){
		// 通常业务
		JSONObject jsonTarget = appResult.getJsonTarget();
		if(jsonTarget == null){
			jsonTarget = new JSONObject(true);
			LoggerAspect.logWarn("jsonTarget == null");
		}
		jsonTarget.put("msg", appResult.getMsg());
		jsonTarget.put("code", appResult.getCode());
		jsonTarget.put("totalnum", String.valueOf(appResult.getTotalnum()));
		jsonTarget.put("totalpage", String.valueOf(0));
		jsonTarget.put("listtype", String.valueOf(appResult.getListtype()));
		if(!(appResult instanceof ComplexAppResultEntity) && appResult.getJsonSData() == null){
			appResult.setJsonSData(new JSONArray());
			if(appResult.getJsonTData() != null){
				ExceptionService.throwCodeException("SourceData == null但TargetData != null");
			}
			appResult.setJsonTData(appResult.getJsonSData());
		}
		jsonTarget.put("data", appResult.getJsonTData());
	}
	
	public static void setResultError(BaseAppResultEntity appResult, BaseAppReqEntity appReq, String msg){
		// 先简单设置吧
		appResult.setPcReqOK(false);
		appResult.setCode("-1");
		if(msg == null){
			appResult.setMsg("PC服务器返回为空");
		}
		else{
			appResult.setMsg(msg);
		}
		appResult.setTotalnum(0);
		appResult.setTotalpage(0);
		if(appResult.getJsonTarget() == null){
			JSONObject jsonTarget = new JSONObject(true);
			appResult.setJsonTarget(jsonTarget);
		}
		// 复杂请求的jsonSource无意义
		if(!(appResult instanceof ComplexAppResultEntity) && (appResult.getJsonSource() == null)){
			appResult.setJsonSource(appResult.getJsonTarget());
		}
		buildAppResultJson(appResult, appReq);
	}
	
//	private static BaseAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, ReqIntfEnums type, String serviceKey, JSONObject pcJson){ 
////		ReqIntfEnums type = serverReq.getReqIntf();
//		BaseAppResultEntity appResult = getAppResultEntity(type);
////		appResult.setServerReq(serverReq);
//		JSONObject jsonTarget = null;
////		String serviceKey = serverReq.getServiceKey();
//		if(serviceKey == null || serviceKey.equals("")){
//			ExceptionService.throwCodeException("无法找到前端传的servicekey");
//		}
//		// 判断PC端返回的报文格式是否正确
//		if((type != ReqIntfEnums.ErrIntf) && (pcJson != null)){
//			appResult.setPcReqOK(true);
//			appResult.setJsonSource(pcJson);
//			jsonTarget = new JSONObject(true);
//			appResult.setJsonTarget(jsonTarget);
//			init(appResult, type, serviceKey);
//			afterInit(appResult, type, serviceKey);
//			buildAppResultJson(appResult, appReq);
//		}
//		else{
//			setResultError(appResult, appReq, null);
//		}
//		return appResult;
//	}
	/**
	 * 处理App常规请求
	 * @param appReq 允许为null
	 * @param serviceKey 需存在对应的配置文件
	 * @param pcResult 允许为null
	 * @return
	 */
	public static BaseAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, 
			String serviceKey, JSONObject pcJson){ 
		ReqIntfEnums type = ReqJsonFilesProp.getReqIntfEnums(serviceKey);
		if(serviceKey == null || serviceKey.equals("") || type == ReqIntfEnums.ErrIntf){
			ExceptionService.throwCodeException("无法找到前端传的servicekey, 或无法找到ReqIntfEnums");
			return null;
		}
		BaseAppResultEntity appResult = getAppResultEntity(type);
//		appResult.setServerReq(null);
		JSONObject jsonTarget = null;
		// 判断PC端返回的报文格式是否正确
		if(pcJson != null){
			appResult.setPcReqOK(true);
			appResult.setJsonSource(pcJson);
			jsonTarget = new JSONObject(true);
			appResult.setJsonTarget(jsonTarget);
			init(appResult, type, serviceKey);
			afterInit(appResult, type, serviceKey);
			buildAppResultJson(appResult, appReq);
		}
		else{
			// 先简单设置吧
			appResult.setPcReqOK(false);
			appResult.setCode("-1");
			appResult.setMsg("PC服务器返回为空");
			appResult.setTotalnum(0);
			appResult.setTotalpage(0);
			jsonTarget = new JSONObject(true);
			appResult.setJsonSource(jsonTarget);
			appResult.setJsonTarget(jsonTarget);
			buildAppResultJson(appResult, appReq);
		}
		return appResult;
	}
	/**
	 * 处理App单次请求，后端多次PC请求的情况
	 * @param appReq
	 * @param serviceKeys
	 * @param pcJsons
	 * @return
	 */
	public static ComplexAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, 
			ArrayList<JSONObject> pcJsons){ 
		ComplexAppResultEntity appResult = null;
		String errmsg = "";
		try{
			// 校验serviceKey、type等
			String serviceKey = appReq.getServiceKey();
			ReqIntfEnums type = ReqJsonFilesProp.getReqIntfEnums(serviceKey);
			if(serviceKey == null || serviceKey.equals("") || type == ReqIntfEnums.ErrIntf){
				errmsg = "无法找到前端传的servicekey, 或无法找到ReqIntfEnums";
				return appResult;
			}
			
			ReqJsonFileEntity objFile = ReqJsonFilesProp.getJsonFileEntity(serviceKey);
			if(objFile == null){
				errmsg = "不支持该接口，未找到对应的配置文件" + serviceKey;
				return appResult;
			}
			ArrayList<String> serviceKeys = objFile.getAsReqServiceKeys();
			if(serviceKeys == null || serviceKeys.size() < 1){
				errmsg = "请求参数错误！" + serviceKey;
				return appResult;
			}
			for(int i = 0; i < serviceKeys.size(); i++){
				String item = serviceKeys.get(i);
				if(item == null || item.equals("")){
					errmsg = "请求参数为空或null！" + serviceKey;
					return appResult;
				}
			}
			if(pcJsons == null || serviceKeys.size() != pcJsons.size()){
				errmsg = "无法处理pcJsons为null, 或请求对象个数，和请求结果数量不等";
				return appResult;
			}
			// 创建ComplexAppResultEntity对象
			BaseAppResultEntity objBase = getAppResultEntity(type);
			if(objBase instanceof ComplexAppResultEntity){
				appResult = (ComplexAppResultEntity)objBase;
			}else{
				errmsg = "该请求类型不支持多次请求 " + objBase.toString();
				return appResult;
			}
			// 初始化
			ArrayList<BaseAppResultEntity> subResults = new ArrayList<BaseAppResultEntity>();
			appResult.setSubResults(subResults);
			JSONObject jsonTarget = null;
			for(int i = 0; i < serviceKeys.size(); i++){
				BaseAppResultEntity subResult = null;
				try{
					subResult = getAppResultEntity(appReq, serviceKeys.get(i), pcJsons.get(i));
				}catch(Exception e){
					ExceptionService.throwCodeException("无法生成复杂请求中的子请求 = " + serviceKeys.get(i));
				}
				subResults.add(subResult);
			}
			if(checkResultsSuccess(subResults)){
				appResult.setPcReqOK(true);
				if(appResult.getJsonTarget() == null){
					jsonTarget = new JSONObject(true);
					appResult.setJsonTarget(jsonTarget);
				}
				init((ComplexAppResultEntity)appResult);
				afterInit(appResult, type, serviceKeys);
				buildAppResultJson(appResult, appReq);
			}else{
				setResultError(appResult, appReq, "复杂请求结果判断为失败！");
			}	
		}finally{
			if(appResult == null){
				LoggerAspect.logError(errmsg);
				appResult = getErrComplexResultEntity(errmsg);
			}
		}
		return appResult;
	}
	
	public static BaseAppResultEntity getAppResultEntity(String serviceKey, JSONObject pcJson){ 
		return getAppResultEntity(null, serviceKey, pcJson);
	}
	
	private static boolean checkResultsSuccess(ArrayList<BaseAppResultEntity> results){
		if(results == null || results.size() < 1){
			return false;
		}
		int errcount = 0;
		for(int i = 0; i < results.size(); i++){
			BaseAppResultEntity item = results.get(i);
			if(item == null){
				LoggerAspect.logError("复杂请求中存在PC请求失败的情况(null)");
				return false;
			}
			if(item.getJsonTData() == null){
				LoggerAspect.logError("复杂请求中子请求data为null（该变量应该创建）");
				return false;
			}
			if(!item.isPcReqOK()){
				errcount++;
				LoggerAspect.logWarn("复杂请求中子请求未成功0");
				continue;
			}
			ResultCodeService.processCodeNode(item);
			if(!ResultCodeService.isSuccess(item, false)){
				errcount++;
				LoggerAspect.logError("复杂请求中子请求未成功1");
			}
		}
		boolean result = errcount < results.size();
		return result;
	}

}
