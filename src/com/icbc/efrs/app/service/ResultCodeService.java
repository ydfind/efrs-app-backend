package com.icbc.efrs.app.service;

import java.util.ArrayList;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.aspect.LoggerAspect;
import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.domain.ComplexAppResultEntity;

public class ResultCodeService {
	
	public static boolean isSuccess(JSONObject root){
		return root.getString("respCode").equals("success");
	}
	
	public static boolean isSuccess(BaseAppResultEntity appResult, boolean ignoreNull){
		if(appResult == null){
			return false;
		}
		if(appResult.getJsonTarget() == null && ignoreNull){
			return true;
		}else{
			return isSuccess(appResult.getJsonTarget());
		}
	}
	
	public static void processCodeNode(JSONObject source, JSONObject target) {
		String code1 = "respCd";
		String Errmsg = "";
		String respCode = "";
		String Msg = "";
		String code = "";
		target.remove("code");
		// 存在respCd，代表请求失败
		// 1.存在"respCd"
		// 若 = "0000"，则："respCd"、"respCode"、"Errmsg"
		// 否则：                            "respCd"、"respCode"、"Errmsg"、"msg"
		if (source.containsKey(code1)) {
			code = source.getString("respCd");
			// code="0000"
			if (code.equals("0000")) {// DRD content: 什么情况下返回"0000", 却存在"respCd"节点
				Errmsg = "";
				respCode = "success";
				target.put("respCd", code);
				target.put("respCode", respCode);
				target.put("Errmsg", Errmsg);
			} else {
				if (source.containsKey("MSG")) {
					Msg = source.getString("MSG");
				} else {
					Msg = source.getString("msg");
				}
				Errmsg = Msg;
				respCode = "failed";
				target.put("respCd", code);
				target.put("respCode", respCode);
				target.put("Errmsg", Errmsg);
				target.put("msg", Msg);
			}
		} else {
			// 判断应答码大小写
			if (source.containsKey("CODE")) {
				code = source.getString("CODE");
			} else {
				code = source.getString("code");
			}

			// 应答码枚举
			if (code.equals("400") || code.equals("404") || code.equals("444")
					|| code.equals("445") || code.equals("703")) {
				if (source.containsKey("MSG")) {
					Msg = source.getString("MSG");
				} else {
					Msg = source.getString("msg");
				}
				Errmsg = Constants.RES_QUERY_NO_INFO;
				respCode = "failed";
				target.put("CODE", code);
				target.put("respCode", respCode);
				target.put("Errmsg", Errmsg);
				target.put("msg", Msg);
			} else if (code.equals("200")) {
				Errmsg = "";
				respCode = "success";
				target.put("respCode", respCode);
				target.put("Errmsg", Errmsg);
			} else {
				if (source.containsKey("MSG")) {
					Msg = source.getString("MSG");
				} else {
					Msg = source.getString("msg");
				}

				Errmsg = Constants.RES_DEF_ERROR_DES;
				respCode = "failed";
				target.put("respCode", respCode);
				target.put("Errmsg", Errmsg);
				target.put("msg", Msg);
			}
		}
	}

	public static void processCodeNode(BaseAppResultEntity appResult) {
		if(appResult instanceof ComplexAppResultEntity){ 
			ArrayList<BaseAppResultEntity> subResults = ((ComplexAppResultEntity)appResult).getSubResults();
			for(int i = 0; i < subResults.size(); i++){
				BaseAppResultEntity subResult = subResults.get(i);
				JSONObject jsonSource = subResult.getJsonSource();
				JSONObject jsonTarget = appResult.getJsonTarget();

				// 默认以最后一个返回结果为准
				processCodeNode(jsonSource, jsonTarget);
				if(subResult == null || !subResult.isPcReqOK() || subResult.getJsonTData() == null){
					LoggerAspect.logError("经营异常中存在PC请求失败的情况(null)");
					break;
				}
				ResultCodeService.processCodeNode(subResult.getJsonSource(), appResult.getJsonTarget());
				if(!ResultCodeService.isSuccess(appResult.getJsonTarget())){
					LoggerAspect.logError("经营异常中存在pc请求失败的情况(Code解析为失败)");
					break;
				}
			}
		}else{
			JSONObject jsonSource = appResult.getJsonSource();
			JSONObject jsonTarget = appResult.getJsonTarget();
			processCodeNode(jsonSource, jsonTarget);
		}
	}
	// // 测试使用
	// public static void main(String[] args){
	// JSONObject jsonTarget = new JSONObject(true);
	// JSONObject jsonSource = new JSONObject(true);
	//		
	// jsonSource.put("CODE", "000");
	// jsonTarget.put("code", "000");
	// processCodeNode(jsonSource, jsonTarget);
	// }
}
