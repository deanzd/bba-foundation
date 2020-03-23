package com.eking.momp.controller;

import com.eking.momp.model.service.EntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eking.momp.model.dto.ModelEntitiesDto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v1/dimensions")
@Api(tags = "视角")
public class DimensionController {

	@Autowired
	private EntityService entityService;

	@GetMapping("/{dimensionId}/model-entities")
	@ApiOperation("根据维度获取模型实例信息")
	public ModelEntitiesDto getModelEntities(
			@ApiParam("业务视角Id") @PathVariable Integer dimensionId,
			@ApiParam("搜索关键字") @RequestParam(required = false) String keyword) {
		return entityService.getModelEntities(dimensionId, keyword);
	}
}
