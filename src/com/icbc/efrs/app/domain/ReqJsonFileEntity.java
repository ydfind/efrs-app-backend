package com.icbc.efrs.app.domain;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.exception.UDException;
import com.icbc.efrs.app.utils.FileUtil;

public class ReqJsonFileEntity {
	private static String nodeParamName = "serverParams";
	private static String nodeTranName = "serverTranKeys";
	private static String nodeTopKeysName = "asTopKeys";
	private static String nodeDelKeysName = "asDelKeys";
	private static String splitStr = "-";
	private String filename;// 文件名称
	private String reqKey;  // 请求的key
	private ReqIntfEnums reqType;
	private ArrayList<String> reqParams;
	private ArrayList<String> reqTranKeys;
	private JSONObject jsonObject;
	private ArrayList<String> asTopKeys;
	private ArrayList<String> asDelKeys;
	
	public ReqJsonFileEntity(String filename){
		this.filename = filename;
		setReqParams(new ArrayList<String>());
		reqTranKeys = new ArrayList<String>();
		asTopKeys = new ArrayList<String>();
		asDelKeys = new ArrayList<String>();
		setReqType(ReqIntfEnums.ErrIntf);
		init();
	}
	
	private void init(){
		int lastindex, temp;
		temp = filename.lastIndexOf("/");
		lastindex = filename.lastIndexOf("\\");
		if(lastindex < temp)
			lastindex = temp;
		String[] strs = filename.substring(lastindex + 1).split(splitStr);
		try{
			if(strs.length != 3)
				throw new Exception("解析错误");
			// 请求的关键字
			reqKey = strs[0];
			// 请求的接口类型
			int reqTypeId = Integer.parseInt(strs[1]);
			for(ReqIntfEnums e: ReqIntfEnums.values())
				if(e.getID() == reqTypeId){
					setReqType(e);
					break;
				}
			if(getReqType() == ReqIntfEnums.ErrIntf){
				throw new Exception("解析错误");
			}
			
			String ret = FileUtil.getContent(filename);
// 默认都是正确的格式
//			if (CoreUtils.nullSafeSize(ret) == 0) {
//				return ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();
//			}
			jsonObject = JSON.parseObject(ret, Feature.OrderedField);
			// 读取参数节点
			String paramstr;
			String[] paramstrs;
			String nodeName = "";
			getReqParams().clear();
			if(jsonObject.containsKey(nodeParamName)){
				paramstr = jsonObject.getString(nodeParamName);
				paramstrs = paramstr.split(splitStr);
				for(int i = 0; i < paramstrs.length; i++){
					getReqParams().add(paramstrs[i]);
				}
				jsonObject.remove(nodeParamName);
			}
			// 读取节点
			reqTranKeys.clear();
			if(jsonObject.containsKey(nodeTranName)){
				paramstr = jsonObject.getString(nodeTranName);
			    paramstrs = paramstr.split(splitStr);
				for(int i = 0; i < paramstrs.length; i++){
					reqTranKeys.add(paramstrs[i]);
				}
				jsonObject.remove(nodeTranName);
			}
			// 读取前置节点
			asTopKeys.clear();
			nodeName = nodeTopKeysName;
			if(jsonObject.containsKey(nodeName)){
				paramstr = jsonObject.getString(nodeName);
			    paramstrs = paramstr.split(splitStr);
				for(int i = 0; i < paramstrs.length; i++){
					asTopKeys.add(paramstrs[i]);
				}
				jsonObject.remove(nodeName);
			}
			// 读取删除节点
			asDelKeys.clear();
			nodeName = nodeDelKeysName;
			if(jsonObject.containsKey(nodeName)){
				paramstr = jsonObject.getString(nodeName);
			    paramstrs = paramstr.split(splitStr);
				for(int i = 0; i < paramstrs.length; i++){
					asDelKeys.add(paramstrs[i]);
				}
				jsonObject.remove(nodeName);
			}
		}catch(Exception e){
			System.out.println("错误的文件名：" + filename);
			e.printStackTrace();
		}
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setReqKey(String reqKey) {
		this.reqKey = reqKey;
	}

	public String getReqKey() {
		return reqKey;
	}

	public void setJsonObject(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public void setReqType(ReqIntfEnums reqType) {
		this.reqType = reqType;
		System.out.println("----------当前类型为：" + reqType);
	}

	public ReqIntfEnums getReqType() {
		return reqType;
	}

	public void setReqParams(ArrayList<String> reqParams) {
		this.reqParams = reqParams;
	}

	public ArrayList<String> getReqParams() {
		return reqParams;
	}

	public void setAsTopKeys(ArrayList<String> asTopKeys) {
		this.asTopKeys = asTopKeys;
	}

	public ArrayList<String> getAsTopKeys() {
		return asTopKeys;
	}

	public void setAsDelKeys(ArrayList<String> asDelKeys) {
		this.asDelKeys = asDelKeys;
	}

	public ArrayList<String> getAsDelKeys() {
		return asDelKeys;
	}
}
