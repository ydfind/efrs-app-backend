package com.icbc.efrs.app.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.icbc.efrs.app.domain.Result;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.exception.UDException;
import com.icbc.efrs.app.utils.ResultUtils;

@ControllerAdvice
public class ExceptionHandle {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionHandle.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result handle(Exception e){
        if(e instanceof UDException){
            return ResultUtils.fail(((UDException) e).getCode(),e.getMessage());
        }else{
            logger.error("系统错误：{}",e);
//            return ResultUtils.fail(ResultCodeEnums.UNKNOWN_ERROR.getCode(),ResultCodeEnums.UNKNOWN_ERROR.getMsg());
            return ResultUtils.fail(ResultCodeEnums.UNKNOWN_ERROR.getCode(),e.getMessage());
        }
    }
}
