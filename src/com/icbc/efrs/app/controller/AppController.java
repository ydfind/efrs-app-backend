package com.icbc.efrs.app.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.icbc.efrs.app.enums.ResultCodeEnums;
import com.icbc.efrs.app.utils.CoreUtils;
import com.icbc.efrs.app.utils.FileUtil;
import com.icbc.efrs.app.utils.ResultUtils;

@RestController
@RequestMapping("/app")
@Api(tags="移动端api服务")
public class AppController {

	/**
	 * 根据name上下文根，获取结果json
	 * 
	 * @param paramStr
	 * @return
	 * @throws IOException
	 */
	@PostMapping(value = "/json/{name}", produces = "applicatoin/json;charset=utf-8")
	@ApiOperation(httpMethod = "POST", value = "请求后端Json", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getJson(
			@PathVariable(value = "name", required = true) String name,
			HttpServletRequest request) {
		String realPath = request.getSession().getServletContext().getRealPath(
				"/");
		if (realPath != null && !realPath.endsWith(File.separator)) {
			realPath = realPath + File.separator;
		}
		// TODO 临时写死取文件，后续请调整
		String filePath = realPath + "json" + File.separator + name + ".json";
		String ret = FileUtil.getContent(filePath);
		if (CoreUtils.nullSafeSize(ret) == 0) {
			return ResultUtils.pick(ResultCodeEnums.FILE_NOT_EXISTS).toString();
		}
		return ret;
	}
}
