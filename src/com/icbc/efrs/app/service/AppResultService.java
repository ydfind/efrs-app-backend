package com.icbc.efrs.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.domain.BaseAppReqEntity;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.BaseServerReqEntity;
import com.icbc.efrs.app.domain.FengXianAppResultEntity;
import com.icbc.efrs.app.domain.ReqJsonFileEntity;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.prop.ReqJsonFilesProp;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * 构建返回APP结果对象
 * @author kfzx-dengrd
 *
 */
public class AppResultService {
	// 工厂：根据type创建，app请求返回对象
	private static BaseAppResultEntity getAppResultEntity(ReqIntfEnums type){
		BaseAppResultEntity objAppRet = null;
		switch(type){
		case FaHai:;
		case FuzzyQuery:
		case ZhongShu:
		case CompanyReport:
		case ZSWithParamNoPaged:
			objAppRet = new BaseAppResultEntity();
			break;
		case FXWithParam:
		case FXWithParams:
			objAppRet = new FengXianAppResultEntity();
			break;
		case CompanyQuery:
			ExceptionService.throwCodeException("无法识别此接口类型CompanyQuery");
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
		}
		return objAppRet;
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
			result = "CODE";
			break;
		case FXWithParam:
		case FXWithParams:
			result = "respCd";
			break;
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
			result = "MSG";
			break;
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
		default:
			ExceptionService.throwCodeException("无法识别此接口类型的总数量key");
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
		}else{
			appResult.setMsg("PC服务器错误：找不到后端返回的msg节点");
		}
		// code节点
		key = getCodeKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			appResult.setCode(jsonSource.getString(key));
		}else{
			appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
			appResult.setMsg("PC服务器错误：找不到后端返回的成功码节点");
		}
		// totalNum
		key = getTotalNumKey(type);
		if((key.length() > 0) && jsonSource.containsKey(key)){
			int totalnum = JSONUtil.getIntegerByKey(jsonSource, key, 0);
			appResult.setTotalnum(totalnum);
		}else{// 企业年报没有totalnum
			appResult.setTotalnum(0);
		}
		// Data节点识别
		if(objFile != null && (objFile.getAsDataKeys().size() == 1)){
			String datakey = objFile.getAsDataKeys().get(0);
			Object dataValue = JSONUtil.getSubNodeObj(jsonSource, datakey);
			if(dataValue != null){
				appResult.setJsonSData(dataValue);
				appResult.setJsonTData(dataValue);
			}else{
				//appResult.setCode("-1");// DRD: 找不到节点，默认返回错误
				appResult.setJsonSData(null);
				appResult.setJsonTData(null);
				if(type != ReqIntfEnums.FuzzyQuery){
					ExceptionService.throwCodeException("无法从后台服务器返回报文中找到data节点, = " + datakey);
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
				if(type != ReqIntfEnums.FuzzyQuery){
					ExceptionService.throwCodeException("无法从后台服务器返回报文中找到data节点");
				}
			}
		}
	}

	// 进行json的组装
	private static void afterInit(BaseAppResultEntity appResult, ReqIntfEnums type, String serviceKey){
//		int listtype = 0;//1----企业年报查询;2----动产抵押查询;3----风险有数据，无明细；4----风险无数据
		// 特殊业务
		switch(type){
		case FaHai:
		case ZhongShu:
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
				if(serviceKey.equals(BaseAppReqEntity.NAME_ZS_ZMXX) && jsonObject.size() > 0){
					totalnum = 1;
				}
			}
			appResult.setTotalnum(totalnum);
			break;
		default:
			ExceptionService.throwCodeException("无法识别此接口类型1");
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
		    		if(objAcc instanceof JSONArray){
		    			if(listtype == 3)// 有数据、有明细
		    				listtype = 0;
		    			JSONUtil.copyJSONArray((JSONArray)objAcc, newData);
		    		}
		    		else
		    			ExceptionService.throwCodeException("风险找到的aclist节点不是JSONArray类型");
		    	}
		    }
		    totalnum = newData.size();
		    appResult.setJsonTData(newData);
		}
		else{
			ExceptionService.throwCodeException("无法找到风险带参数接口，从PC端返回的data节点");
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
			System.out.println("---------查找到的年份为：" + entry.getKey() + "; 及value为：" + entry.getValue());
		}
		System.out.println("------最终结果为：" + jsonTData.toString());
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
	private static void buildAppResultJson(BaseAppResultEntity appResult, BaseAppReqEntity appReq){
		buildAppResultJson(appResult);
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
	}
	
	private static void buildAppResultJson(BaseAppResultEntity appResult){
		// 通常业务
		JSONObject jsonTarget = appResult.getJsonTarget();
		jsonTarget.put("msg", appResult.getMsg());
		jsonTarget.put("code", appResult.getCode());
		jsonTarget.put("totalnum", String.valueOf(appResult.getTotalnum()));
		jsonTarget.put("totalpage", String.valueOf(0));
		jsonTarget.put("listtype", String.valueOf(appResult.getListtype()));
		if(appResult.getJsonSData() == null){
			appResult.setJsonSData(new JSONObject(true));
			if(appResult.getJsonTData() != null){
				ExceptionService.throwCodeException("SourceData == null但TargetData != null");
			}
			appResult.setJsonTData(appResult.getJsonSData());
		}
		jsonTarget.put("data", appResult.getJsonTData());
	}
	
	public static BaseAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, ReqIntfEnums type, String serviceKey, JSONObject pcJson){ 
//		ReqIntfEnums type = serverReq.getReqIntf();
		BaseAppResultEntity appResult = getAppResultEntity(type);
//		appResult.setServerReq(serverReq);
		JSONObject jsonTarget = null;
//		String serviceKey = serverReq.getServiceKey();
		if(serviceKey == null || serviceKey.equals("")){
			ExceptionService.throwCodeException("无法找到前端传的servicekey");
		}
		// 判断PC端返回的报文格式是否正确
		if((type != ReqIntfEnums.ErrIntf) && (pcJson != null)){
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
			buildAppResultJson(appResult, appReq);
		}
		return appResult;
	}
	// appReq可能为null
	public static BaseAppResultEntity getAppResultEntity(BaseAppReqEntity appReq, String serviceKey, JSONObject pcJson){ 
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
			buildAppResultJson(appResult, appReq);
		}
		return appResult;
	}
	
	public static BaseAppResultEntity getAppResultEntity(String serviceKey, JSONObject pcJson){ 
		return getAppResultEntity(null, serviceKey, pcJson);
	}

}
