package com.icbc.efrs.app.utils;

import java.util.Arrays;

public class CheckUtil {
    private static final String token = "token";
    public static boolean checkSignature(String signature, String timestamp, String nonce){
        String[] arr = new String[]{token,timestamp,nonce};
        //排序
        Arrays.sort(arr);

        //生成字符串
        StringBuffer content = new StringBuffer();
        for (String str:arr){
            content.append(str);
        }

        //sha1加密
        String temp = Sha1Utils.getSha1(content.toString());

        return temp.equals(signature);
    }
}
