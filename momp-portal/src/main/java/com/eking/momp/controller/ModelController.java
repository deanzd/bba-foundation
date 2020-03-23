package com.eking.momp.controller;

import java.util.List;

import com.eking.momp.model.dto.ModelTopoDto;
import com.eking.momp.model.dto.ModelRelationTopoDto;
import com.eking.momp.model.dto.TopoDto;
import com.eking.momp.model.manager.ModelManager;
import com.eking.momp.model.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.param.ModelParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api/v1/models")
@Api(tags = "模型")
public class ModelController {
    @Autowired
    private ModelService modelService;
    @Autowired
    private ModelManager modelManager;

    @GetMapping("")
    @ApiOperation("获取模型信息列表")
    public List<ModelDto> list(@ApiParam("搜索关键字") @RequestParam(required = false) String keyword) {
        return modelManager.list(keyword);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取模型信息")
    public ModelDto getById(@ApiParam("模型ID") @PathVariable Integer id) {
        return modelService.getById(id);
    }

    @PostMapping("")
    @ApiOperation("添加模型")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public ModelDto save(@ApiParam("模型属性") @Validated() @RequestBody ModelParam param) {
        return modelService.save(param);
    }

    @PutMapping("/{id}")
    @ApiOperation("修改模型")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public ModelDto update(
            @ApiParam("模型ID") @PathVariable Integer id,
            @ApiParam("模型属性") @RequestBody ModelParam param) {
        return modelService.update(id, param);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除模型")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public Integer delete(@ApiParam("模型ID") @PathVariable Integer id) {
        modelService.delete(id);
        return id;
    }

    @GetMapping("/{id}/topo")
    @ApiOperation("模型拓扑")
    public TopoDto<List<ModelTopoDto>, List<ModelRelationTopoDto>> getModelTopo(
            @ApiParam("模型ID") @PathVariable Integer id) {
        return modelManager.getModelTopo(id);
    }
}
