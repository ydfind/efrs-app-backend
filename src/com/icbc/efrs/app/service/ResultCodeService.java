package com.icbc.efrs.app.service;

import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.domain.BaseAppResultEntity;

public class ResultCodeService {

	private static void processCodeNode(JSONObject source, JSONObject target) {
		String code1 = "respCd";
		String Errmsg = "";
		String respCode = "";
		String Msg = "";
		String code = "";
		target.remove("code");
		// 存在respCd，代表请求失败
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
				Errmsg = "没有查到满足条件的信息";
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

				Errmsg = "查询过程中出现异常";
				respCode = "failed";
				target.put("respCode", respCode);
				target.put("Errmsg", Errmsg);
				target.put("msg", Msg);
			}
		}
	}

	public static void processCodeNode(BaseAppResultEntity appResult) {
		// 
		JSONObject jsonTarget = appResult.getJsonTarget();
		JSONObject jsonSource = appResult.getJsonSource();
		processCodeNode(jsonSource, jsonTarget);
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
