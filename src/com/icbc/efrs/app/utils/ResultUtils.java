package com.icbc.efrs.app.utils;

import com.icbc.efrs.app.domain.Result;
import com.icbc.efrs.app.enums.ResultCodeEnums;

public class ResultUtils {
    public static Result success(String msg) {
        Result result = new Result();
        result.setCode(ResultCodeEnums.SUCCESS.getCode());
        result.setMsg(ResultCodeEnums.SUCCESS.getMsg() + "," + msg);
        return result;
    }

    public static Result fail(Integer code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    
    public static Result pick(ResultCodeEnums enums) {
        Result result = new Result(enums);
        return result;
    }
}
