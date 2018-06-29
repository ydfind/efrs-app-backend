package com.icbc.efrs.app.domain;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.exception.UDException;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.CoreUtils;
import com.icbc.efrs.app.utils.FileUtil;
/*
 * App请求对应的jsonfile文件类
 */
public class ReqJsonFileEntity {
	private static String nodeParamName = "asParams";          // 不同接口按约定传输的params
	private static String nodeTranName = "serverTranKeys";     // 
	private static String nodeTopKeysName = "asTopKeys";       // 需要置顶的字段
	private static String nodeDelKeysName = "asDelKeys";       // 需要删除的字段（不显示字段）
	private static String nodePagePathName = "asPagePath";     // page在请求中节点位置
	private static String nodePageOffsetName = "asPageOffset"; // PC接口page从0开始的，需要设置为-1
	private static String nodeDataKeysName = "asDataKeys";     // 为data的节点，目前仅支持单节点
	private static String nodePagedName = "asPaged";           // -1：强制不分页；0：；1：强制分页；
	private static String nodeFXParmsName = "asFXParams";      // 中数带的多个参数:布控类型集合
	private static String splitStr = "-";
	private String filename;// 文件名称
	private String reqKey;  // 请求的key
	private ReqIntfEnums reqType;
	private ArrayList<String> asParams;
	private ArrayList<String> reqTranKeys;
	private JSONObject jsonObject;
	private ArrayList<String> asTopKeys;
	private ArrayList<String> asDelKeys;
	private String asPagePath;
	private Integer asPageOffset;                   // 为0或-1，默认为0
	private ArrayList<String> asDataKeys;
	private Integer asPaged;                        // -1：强制不分页；0：；1：强制分页；
	private ArrayList<String> asFXParams;
	
	public ReqJsonFileEntity(String filename){
		this.filename = filename;
		asParams = new ArrayList<String>();
		reqTranKeys = new ArrayList<String>();
		asTopKeys = new ArrayList<String>();
		asDelKeys = new ArrayList<String>();
		asFXParams = new ArrayList<String>();
		setAsDataKeys(new ArrayList<String>());
		setReqType(ReqIntfEnums.ErrIntf);
		setAsPageOffset(0);
		setAsPagePath("");
		jsonObject = null;
		reqKey = "";
		asPaged = 0;
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
			if (CoreUtils.nullSafeSize(ret) == 0) {
				ExceptionService.throwCodeException("appserver.json内容为空！" + reqKey);
			}
			else{
				jsonObject = JSON.parseObject(ret, Feature.OrderedField);
				initNodes(jsonObject);
			}
			
		}catch(Exception e){
			System.out.println("错误的文件名：" + filename);
			e.printStackTrace();
		}
	}
	
	private void initNodes(JSONObject jsonObject){
		// 读取参数节点
		String paramstr;
		String[] paramstrs;
		String nodeName = "";
		loadAndDelNode(asParams, nodeParamName);
		// 读取节点
		loadAndDelNode(reqTranKeys, nodeTranName);
		// 读取前置节点
		loadAndDelNode(asTopKeys, nodeTopKeysName);
		// 读取删除节点
		loadAndDelNode(asDelKeys, nodeDelKeysName);
		// 读取data的默认节点
		loadAndDelNode(asDataKeys, nodeDataKeysName);
		loadAndDelNode(asFXParams, nodeFXParmsName);
		// 读取page偏移量相关、是否分页、page的路径
		asPageOffset = loadAndDelNode(nodePageOffsetName, asPageOffset);
		asPaged = loadAndDelNode(nodePagedName, asPaged);
		asPagePath = loadAndDelNode(nodePagePathName, asPagePath);
	}
	/**
	 * 加载节点nodeName中数据到strs，并删除该节点
	 * @param strs 加载的参数集合
	 * @param nodeName json中节点key名称
	 */
	private void loadAndDelNode(ArrayList<String> strs, String nodeName){
		String paramstr;
		String[] paramstrs;
		strs.clear();
		if(jsonObject.containsKey(nodeName)){
			paramstr = jsonObject.getString(nodeName);
		    paramstrs = paramstr.split(splitStr);
			for(int i = 0; i < paramstrs.length; i++){
				strs.add(paramstrs[i]);
			}
			jsonObject.remove(nodeName);
		}	
	}
	/**
	 * 加载节点nodeName中数据，并返回，同时删除该节点
	 * @param nodeName json中节点key名称
	 * @param def 返回的默认值
	 * @return 返回节点对应的value值
	 */
	private int loadAndDelNode(String nodeName, int def){
		int result = def;
		if(jsonObject.containsKey(nodeName)){
			result = Integer.parseInt(jsonObject.getString(nodeName));
			jsonObject.remove(nodeName);
		}
		return result;
	}
	/**
	 * 加载节点nodeName中数据，并返回，同时删除该节点
	 * @param nodeName json中节点key名称
	 * @param def 返回的默认值
	 * @return 返回节点对应的value值
	 */
	private String loadAndDelNode(String nodeName, String def){
		String result = def;
		if(jsonObject.containsKey(nodeName)){
			result = jsonObject.getString(nodeName);
			jsonObject.remove(nodeName);
		}
		return result;
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
	}

	public ReqIntfEnums getReqType() {
		return reqType;
	}

	public String getAsParam(int id){
		String result = "";
		if(id < asParams.size())
			result = asParams.get(id);
		else
			ExceptionService.throwCodeException("读取请求第" + String.valueOf(id) + "参数失败");
		return result;
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

	public void setAsParams(ArrayList<String> asParams) {
		this.asParams = asParams;
	}

	public ArrayList<String> getAsParams() {
		return asParams;
	}

	public void setAsPagePath(String asPagePath) {
		this.asPagePath = asPagePath;
	}

	public String getAsPagePath() {
		return asPagePath;
	}

	public void setAsPageOffset(Integer asPageOffset) {
		this.asPageOffset = asPageOffset;
	}

	public Integer getAsPageOffset() {
		return asPageOffset;
	}

	public void setAsDataKeys(ArrayList<String> asDataKeys) {
		this.asDataKeys = asDataKeys;
	}

	public ArrayList<String> getAsDataKeys() {
		return asDataKeys;
	}

	public void setAsPaged(Integer asPaged) {
		this.asPaged = asPaged;
	}

	public Integer getAsPaged() {
		return asPaged;
	}

	public void setAsFXParams(ArrayList<String> asFXParams) {
		this.asFXParams = asFXParams;
	}

	public ArrayList<String> getAsFXParams() {
		return asFXParams;
	}
}
