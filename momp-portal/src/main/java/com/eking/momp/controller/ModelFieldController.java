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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eking.momp.model.dto.ModelFieldDto;
import com.eking.momp.model.param.ModelFieldParam;
import com.eking.momp.model.service.ModelFieldService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RequestMapping("/api/v1/model-fields")
@Api(tags = "模型字段")
@RestController
public class ModelFieldController {
    @Autowired
    private ModelFieldService modelFieldService;

    @GetMapping("")
    @ApiOperation("获取模型字段列表")
    public List<ModelFieldDto> listModelFields(
            @ApiParam("模型ID") @RequestParam Integer modelId) {
        return modelFieldService.listByModelId(modelId);
    }

    @GetMapping("/{modelFieldId}")
    @ApiOperation("获取模型字段")
    public ModelFieldDto getModelFieldById(@ApiParam("模型字段ID") @PathVariable Integer modelFieldId) {
        return modelFieldService.getById(modelFieldId);
    }

    @PostMapping("")
    @ApiOperation("添加模型字段")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public ModelFieldDto saveModelField(@ApiParam("模型字段属性") @RequestBody ModelFieldParam param) {
        return modelFieldService.save(param);
    }

    @PutMapping("/{modelFieldId}")
    @ApiOperation("修改模型字段")
	@PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public ModelFieldDto updateModelField(
            @ApiParam("模型字段ID") @PathVariable Integer modelFieldId,
            @ApiParam("模型字段属性") @RequestBody ModelFieldParam param) {
        return modelFieldService.update(modelFieldId, param);
    }

    @DeleteMapping("/{modelFieldId}")
    @ApiOperation("删除模型字段")
	@PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public Integer deleteModelField(@ApiParam("模型字段ID") @PathVariable Integer modelFieldId) {
        modelFieldService.delete(modelFieldId);
        return modelFieldId;
    }
}
