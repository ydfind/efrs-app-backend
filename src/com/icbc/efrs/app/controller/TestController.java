package com.icbc.efrs.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.icbc.efrs.app.domain.Result;
import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.exception.UDException;
import com.icbc.efrs.app.utils.ResultUtils;

@RestController
@RequestMapping("/test")
@Api(tags = "移动端可用性应答服务")
public class TestController {

	/**
	 * 可用性应答
	 * 
	 * @param paramStr
	 * @return
	 */
	@PostMapping(value = "/checkObject")
	@ApiOperation(httpMethod = "POST", value = "请求应答", produces = MediaType.ALL_VALUE)
	public Result checkObject(
			@RequestParam(value = "param", required = false, defaultValue = "checked") String paramStr) {
//		Result<HttpStatus> result = new Result<HttpStatus>();
		if ("null".equalsIgnoreCase(paramStr)) {
			// return new ResponseEntity(HttpStatus.NOT_FOUND);
			throw new UDException(ResultCodeEnums.NULL_VALUE.getCode(),
					ResultCodeEnums.NULL_VALUE.getMsg());
		}
		// return new ResponseEntity(paramStr, HttpStatus.OK);
		return ResultUtils.success(paramStr);
	}

	/**
	 * 可用性应答
	 * 
	 * @param paramStr
	 * @return
	 */
	@GetMapping(value = "/check")
	@ApiOperation(httpMethod = "GET", value = "请求应答", produces = MediaType.ALL_VALUE)
	public String check(
			@RequestParam(value = "param", required = false, defaultValue = "checked") String paramStr) {
		return paramStr;
	}
}
