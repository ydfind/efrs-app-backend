package com.icbc.efrs.app.domain;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.icbc.efrs.app.enums.ReqIntfEnums;
import com.icbc.efrs.app.service.ExceptionService;
import com.icbc.efrs.app.utils.CoreUtils;
import com.icbc.efrs.app.utils.FileUtil;
/*
 * App请求对应的jsonfile文件类
 */
public class ReqJsonFileEntity {
	/*  * key为asXXX表示配置需要的参数，读取后需要删除该节点；asReqXXX表示请求相关参数；asResXXX表示PC请求结果 */
	private static String nodeParamName = "asParams";          // 不同接口按约定传输的params
	private static String nodeTopKeysName = "asTopKeys";       // 需要置顶的字段
	private static String nodeDelKeysName = "asDelKeys";       // 需要删除的字段（不显示字段）
	private static String nodePagedName = "asPaged";           // -1：强制不分页；0：；1：强制分页；
	private static String nodeFXParmsName = "asFXParams";      // 中数带的多个参数:布控类型集合
	// 请求中节点相关
	private static String nodePageKeyName = "asReqPageKey";     // page在请求中节点位置
	private static String nodePageOffsetName = "asReqPageOffset"; // PC接口page从0开始的，需要设置为-1
	private static String nodeReqServiceKeysName = "asReqServiceKeys";
	// PC返回结果的节点相关
	private static String nodeResDataKeysName = "asResDataKeys";     // 为data的节点，目前仅支持单节点
	private static String nodeResTotalnumName = "asResTotalnumKey";
	private static String nodeResCodeKeyName = "asResCodeKey";
	private static String splitStr = "-";
	private String filename;// 文件名称
	private String reqKey;  // 请求的key
	private ReqIntfEnums reqType;
	private ArrayList<String> asParams;
	private JSONObject jsonObject;
	private ArrayList<String> asTopKeys;
	private ArrayList<String> asDelKeys;
	private Integer asPaged;                        // -1：强制不分页；0：；1：强制分页；
	private ArrayList<String> asFXParams;
	// 请求中节点相关
	private String asReqPageKey;
	private Integer asReqPageOffset;                   // 为0或-1，默认为0
	private ArrayList<String> asReqServiceKeys;
	// PC返回结果的节点相关
	private ArrayList<String> asResDataKeys;
	private String asResTotalnumKey;
	private String asResCodeKey;
	
	public ReqJsonFileEntity(String filename){
		this.filename = filename;
		asParams = new ArrayList<String>();
		asTopKeys = new ArrayList<String>();
		asDelKeys = new ArrayList<String>();
		asFXParams = new ArrayList<String>();
		asResDataKeys = new ArrayList<String>();
		asReqServiceKeys = new ArrayList<String>();
		setReqType(ReqIntfEnums.ErrIntf);
		asReqPageOffset = 0;
		asReqPageKey = "";
		jsonObject = null;
		reqKey = "";
		asResTotalnumKey = "";
		asResCodeKey = "";
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
			if(strs.length != 3){
			    ExceptionService.throwCodeException("解析文件失败，filename名称格式不对 = " + filename);
			}
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
				ExceptionService.throwCodeException("解析文件失败，为ErrIntf类型");
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
			ExceptionService.throwCodeException("解析文件失败 = " + filename + "; " + e.getMessage());
		}
	}
	
	private void initNodes(JSONObject jsonObject){
		// 读取参数节点
		String paramstr;
		String[] paramstrs;
		String nodeName = "";
		loadAndDelNode(asParams, nodeParamName);
//		// 读取节点
//		loadAndDelNode(reqTranKeys, nodeTranName);
		// 读取前置节点
		loadAndDelNode(asTopKeys, nodeTopKeysName);
		// 读取删除节点
		loadAndDelNode(asDelKeys, nodeDelKeysName);
		// 读取data的默认节点
		loadAndDelNode(asResDataKeys, nodeResDataKeysName);
		loadAndDelNode(asReqServiceKeys, nodeReqServiceKeysName);
		loadAndDelNode(asFXParams, nodeFXParmsName);
		// 读取page偏移量相关、是否分页、page的路径、totalnum节点
		asReqPageOffset = loadAndDelNode(nodePageOffsetName, asReqPageOffset);
		asPaged = loadAndDelNode(nodePagedName, asPaged);
		setAsReqPageKey(loadAndDelNode(nodePageKeyName, getAsReqPageKey()));
		asResTotalnumKey = loadAndDelNode(nodeResTotalnumName, asResTotalnumKey);
		asResCodeKey = loadAndDelNode(nodeResCodeKeyName, asResCodeKey); 
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

	public void setAsResDataKeys(ArrayList<String> asResDataKeys) {
		this.asResDataKeys = asResDataKeys;
	}

	public ArrayList<String> getAsResDataKeys() {
		return asResDataKeys;
	}

	public void setAsResTotalnumKey(String asResTotalnumKey) {
		this.asResTotalnumKey = asResTotalnumKey;
	}

	public String getAsResTotalnumKey() {
		return asResTotalnumKey;
	}

	public void setAsReqPageKey(String asReqPageKey) {
		this.asReqPageKey = asReqPageKey;
	}

	public String getAsReqPageKey() {
		return asReqPageKey;
	}

	public void setAsReqPageOffset(Integer asReqPageOffset) {
		this.asReqPageOffset = asReqPageOffset;
	}

	public Integer getAsReqPageOffset() {
		return asReqPageOffset;
	}

	public void setAsReqServiceKeys(ArrayList<String> asReqServiceKeys) {
		this.asReqServiceKeys = asReqServiceKeys;
	}

	public ArrayList<String> getAsReqServiceKeys() {
		return asReqServiceKeys;
	}

	public void setAsResCodeKey(String asResCodeKey) {
		this.asResCodeKey = asResCodeKey;
	}

	public String getAsResCodeKey() {
		return asResCodeKey;
	}
}
