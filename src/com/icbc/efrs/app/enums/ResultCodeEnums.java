package com.icbc.efrs.app.enums;

public enum ResultCodeEnums {
	// code<0表示内部错误，此时不要直接把msg给客户看；code>0表示外部错误，此时可以给客户看
    UNKNOWN_ERROR(-1,"未知错误"),
    SUCCESS(0,"成功"),
    NULL_VALUE(1,"参数为空"),
    FILE_NOT_EXISTS(2,"文件不存在")
    ;

    private Integer code;
    private String msg;

    ResultCodeEnums(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ResultCodeEnums{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
    
    
}
