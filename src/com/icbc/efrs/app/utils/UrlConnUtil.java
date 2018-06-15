package com.icbc.efrs.app.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.icbc.efrs.app.enums.RequestMethodEnums;

public class UrlConnUtil {

    private static String prepareParam(Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder();
        if (!paramMap.isEmpty()) {
            for (String key : paramMap.keySet()) {
                String value = (String) paramMap.get(key);
                if (sb.length() < 1) {
                    sb.append(key).append("=").append(value);
                } else {
                    sb.append("&").append(key).append("=").append(value);
                }
            }
        }
        return sb.toString();
    }

    public static String doPostOrGet(String urlStr, Map<String, Object> paramMap, RequestMethodEnums requestMethod) throws Exception {
        String ret = "";
        String paramStr = prepareParam(paramMap);
        if (paramStr.trim().length() >= 1) {
            urlStr += "?" + paramStr;
        }
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod.getCode());
        conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");

        if (HttpStatus.OK.value() == conn.getResponseCode()) {
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while (-1 != (len = is.read(buffer))) {
                baos.write(buffer, 0, len);
                baos.flush();
            }
            ret = baos.toString("utf-8");
            baos.close();
        }
        return ret;
    }


}
