package com.eking.momp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eking.momp.model.dto.ModelUniquenessDto;
import com.eking.momp.model.param.ModelUniquenessParam;
import com.eking.momp.model.service.ModelUniquenessService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RequestMapping("/api/v1/model-uniquenesses")
@Api(tags = "模型唯一性校验")
@RestController
public class ModelUniquenessController {
	
	@Autowired
	private ModelUniquenessService modelUniquenessService;

	@GetMapping("")
	@ApiOperation("获取唯一性校验列表")
	public List<ModelUniquenessDto> listByModelId(Integer modelId) {
		return modelUniquenessService.listByModelId(modelId);
	}

	@GetMapping("/{id}")
	@ApiOperation("根据ID获取唯一性校验")
	public ModelUniquenessDto getById(@PathVariable Integer id) {
		return modelUniquenessService.getById(id);
	}

	@PostMapping("")
	@ApiOperation("添加唯一性校验")
	public ModelUniquenessDto save(@RequestBody ModelUniquenessParam param) {
		return modelUniquenessService.save(param);
	}

	@PutMapping("/{id}")
	@ApiOperation("更新唯一性校验")
	public ModelUniquenessDto update(@PathVariable Integer id, @RequestBody ModelUniquenessParam param) {
		return modelUniquenessService.update(id, param);
	}

	@DeleteMapping("/{id}")
	@ApiOperation("删除唯一性校验")
	public Integer delete(@PathVariable Integer id) {
		modelUniquenessService.delete(id);
		return id;
	}
}
