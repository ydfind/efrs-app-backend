package com.icbc.efrs.app.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONUtil {
    /**
     * string转json
     *
     * @param string 要转换的String
     * @return json
     */
    public static JSON strToJson(String string) {
        JSONObject jsonObj = null;
        try {
            jsonObj = JSON.parseObject(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    /**
     * json转arrayList
     *
     * @param jsonStr 要转换的json
     * @return arrayList
     */
    public static ArrayList<String> jsonStrToAL(String jsonStr) {
        ArrayList list = null;
        try {
            list = JSONObject.parseObject(jsonStr, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json转ListHashMap
     *
     * @param jsonStr 要转换的json
     * @return ListHashMap
     */
    public static LinkedHashMap<String, JSONArray> jsonStrToLHMJsonArray(String jsonStr) {
        LinkedHashMap map = null;
        try {
            map = JSONObject.parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * json转ListHashMap
     *
     * @param jsonStr 要转换的json
     * @return ListHashMap
     */
    public static LinkedHashMap<String, String> jsonStrToLHM(String jsonStr) {
        LinkedHashMap map = null;
        try {
            map = JSONObject.parseObject(jsonStr, LinkedHashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
