package com.icbc.efrs.app.enums;

public enum RequestMethodEnums {
    SERVLET_POST("POST","post请求"),
    SERVLET_GET("GET","get请求"),
    SERVLET_PUT("PUT","put请求"),
    SERVLET_DELETE("DELETE","delete请求"),
    ;

    private String code;
    private String msg;

    RequestMethodEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
