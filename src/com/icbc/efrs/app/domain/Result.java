package com.icbc.efrs.app.domain;

import com.icbc.efrs.app.enums.ResultCodeEnums;

public class Result<T> {
    /** 返回代码 */
    private Integer code;
    /** 返回消息 */
    private String msg;
    /** 返回对象 */
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    
    public Result() {
    }
    
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(ResultCodeEnums enums) {
        this.code = enums.getCode();
        this.msg = enums.getMsg();
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code +
                "\", \"msg\":\"" + msg +
                "\", \"data\":\"" + data +
                "\"}";
    }
}
