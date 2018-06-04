package com.icbc.efrs.app.exception;

public class UDException extends RuntimeException{
	private Integer code;
    private String msg;

    public UDException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
