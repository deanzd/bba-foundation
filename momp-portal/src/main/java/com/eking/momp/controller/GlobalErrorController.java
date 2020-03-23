package com.eking.momp.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eking.momp.common.exception.ApiNotFoundException;

import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/error")
@ApiIgnore
@Api(tags = "统一错误")
public class GlobalErrorController implements ErrorController {
	
	@RequestMapping
	public void error() {
		throw new ApiNotFoundException();
	}

	@Override
	public String getErrorPath() {
		return null;
	}
}
