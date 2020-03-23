package com.eking.momp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eking.momp.model.dto.RelationTypeDto;
import com.eking.momp.model.param.RelationTypeParam;
import com.eking.momp.model.service.RelationTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v1/relation-types")
@Api(tags = "关联关系类型")
public class RelationTypeController {

	@Autowired
	private RelationTypeService relationTypeService;
	
	@GetMapping("")
	@ApiOperation("获取关联关系类型列表")
	public List<RelationTypeDto> list() {
		return relationTypeService.list();
	}

	@GetMapping("/{id}")
	@ApiOperation("根据ID获取关联关系类型")
	public RelationTypeDto getById(@ApiParam("关联关系类型ID") @PathVariable Integer id) {
		return relationTypeService.getById(id);
	}

	@PostMapping("")
	@ApiOperation("添加关联关系类型")
	@PreAuthorize("hasAnyRole('sysadmin', 'admin')")
	public RelationTypeDto save(@RequestBody RelationTypeParam param) {
		return relationTypeService.save(param);
	}

	@PutMapping("/{id}")
	@ApiOperation("更新关联关系类型")
	@PreAuthorize("hasAnyRole('sysadmin', 'admin')")
	public RelationTypeDto update(
			@ApiParam("关联关系类型ID") @PathVariable Integer id, 
			@ApiParam("关联关系属性") @RequestBody RelationTypeParam param) {
		return relationTypeService.update(id, param);
	}

	@DeleteMapping("/{id}")
	@ApiOperation("删除关联关系类型")
	@PreAuthorize("hasAnyRole('sysadmin', 'admin')")
	public Integer delete(@ApiParam("关联关系类型ID") @PathVariable Integer id) {
		relationTypeService.delete(id);
		return id;
	}

}
