package com.eking.momp.controller;

import com.eking.momp.model.dto.LayerDto;
import com.eking.momp.model.param.LayerParam;
import com.eking.momp.model.service.LayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/layers")
@Api(tags = "资源层")
public class LayerController {
    @Autowired
    private LayerService layerService;

    @GetMapping("/models")
    @ApiOperation("获取资源层下模型信息")
    public List<LayerDto> listLayersModels(@ApiParam("搜索关键字") @RequestParam(required = false) String keyword) {
        return layerService.listWithModels(keyword);
    }

    @GetMapping("")
    @ApiOperation("获取资源层列表")
    public List<LayerDto> listLayers() {
        return layerService.list();
    }

    @GetMapping("/{layerId}")
    @ApiOperation("获取资源层")
    public LayerDto getLayerById(@PathVariable Integer layerId) {
        return layerService.getById(layerId);
    }

    @PostMapping("")
    @ApiOperation("添加资源层")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public LayerDto saveLayer(@RequestBody LayerParam param) {
        return layerService.save(param);
    }

    @PutMapping("/{layerId}")
    @ApiOperation("修改资源层")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public LayerDto updateLayer(@PathVariable Integer layerId, @RequestBody LayerParam param) {
        return layerService.update(layerId, param);
    }

    @DeleteMapping("/{layerId}")
    @ApiOperation("删除资源层")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin')")
    public Integer deleteLayer(@PathVariable Integer layerId) {
        layerService.delete(layerId);
        return layerId;
    }
}
