package com.icbc.efrs.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.icbc.efrs.app.constant.Constants;
import com.icbc.efrs.app.domain.BaseAppResultEntity;
import com.icbc.efrs.app.utils.JSONUtil;
/**
 * 对PC返回结果，进行分页处理
 * @author kfzx-dengrd
 *
 */
public class ResultPageService {
	
	public static void page(BaseAppResultEntity appResult, int page, int size){
		Object datanode = appResult.getJsonTData();
		if(datanode instanceof JSONArray){
			JSONArray jsonArray = (JSONArray)datanode;
		    JSONUtil.pagingJson(jsonArray, page, size, Constants.RES_DEF_START_PAGE);
		}else if(datanode instanceof JSONObject){
			JSONObject jsonObject = (JSONObject)datanode;
		    JSONUtil.pagingJson(jsonObject, page, size, Constants.RES_DEF_START_PAGE);
		}
	}

}
