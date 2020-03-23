package com.eking.momp.controller;

import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.param.PageParam;
import com.eking.momp.model.service.EntitySearchService;
import com.eking.momp.model.dto.*;
import com.eking.momp.model.service.EntityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/entities")
@Api(tags = "实例")
public class EntityController {

    @Autowired
    private EntityService entityService;
    @Autowired
    private EntitySearchService entitySearchService;

    @PostMapping("")
    @ApiOperation("创建的实例")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin', 'operator')")
    public Map<String, Object> saveEntity(
            @ApiParam("模型code") @RequestParam String model,
            @ApiParam("参数") @RequestBody Map<String, Object> params) {
        return entityService.save(model, params);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新实例")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin', 'operator')")
    public Map<String, Object> updateEntity(
            @ApiParam("模型code") @RequestParam String model,
            @ApiParam("实例ID") @PathVariable String id,
            @ApiParam("参数") @RequestBody Map<String, Object> params) {
        return entityService.update(model, id, params);
    }

    @GetMapping("/search")
    @ApiOperation("全文检索")
    public PageBean<EntityDto> search(
            @ApiParam(value = "关键字", required = true) @RequestParam String keyword,
            @ApiParam("上一页最后一条ID") @RequestParam(required = false) String lastId) {
        return entityService.search(keyword, lastId);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID获取实例")
    public Map<String, Object> getEntity(
            @ApiParam("实例ID") @PathVariable String id,
            @ApiParam("模型CODE") @RequestParam String model) {
        return entityService.getById(model, id);
    }

    @DeleteMapping("/{ids}")
    @ApiOperation("删除实例")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin', 'operator')")
    public String[] deleteEntity(
            @ApiParam("模型code") @RequestParam String model,
            @ApiParam("实例ID") @PathVariable String... ids) {
        entityService.delete(model, ids);
        return ids;
    }

    @PostMapping("/page")
    @ApiOperation("获取实例分页列表")
    public PageBean<Map<String, Object>> findEntityPage(
            @ApiParam(value = "模型code", required = true) @RequestParam String model,
            @ApiParam("过滤条件") @RequestBody PageParam pageParam) {
        return entityService.page(model, pageParam);
    }

    @GetMapping("/{id}/topo")
    @ApiOperation(value = "获取实例的拓扑信息", notes = "根据实例ID，获取该实例相关的某视角下的实例列表和关系列表")
    public TopoDto<List<ModelEntitiesDto>, List<EntityRelationDto>> getTopo(
            @ApiParam("模型code") @RequestParam String model,
            @ApiParam("实例ID") @PathVariable String id,
            @ApiParam("业务视角") @RequestParam Integer dimensionId) {
        return entityService.getTopo(model, id, dimensionId);
    }

    @GetMapping("/{id}/topo/level2")
    @ApiOperation(value = "获取实例的拓扑信息", notes = "根据实例ID，获取该实例相关的某视角下的实例列表和关系列表")
    public TopoDto<List<ModelEntitiesDto>, List<EntityRelationTopoDto>> getTopoLevel2(
            @ApiParam("模型code") @RequestParam String model,
            @ApiParam("实例ID") @PathVariable String id,
            @ApiParam("源-》目标模型关系ID列表") @RequestParam(required = false) List<Integer> modelRelationIds,
            @ApiParam("目标-》源模型关系ID列表") @RequestParam(required = false) List<Integer> modelRelationIdsR) {
        return entityService.getTopoLevel2(model, id, modelRelationIds, modelRelationIdsR);
    }

    @GetMapping("/export")
    @ApiOperation("导出execl表格")
    public void export(
            @ApiParam(value = "模型code", required = true) @RequestParam String model,
            @ApiParam("过滤关键字") @RequestParam(required = false) String keyword,
            HttpServletResponse response) {
        entityService.export(model, keyword, response);
    }

    @GetMapping("/rebuild-index")
    @ApiOperation("重建es索引")
    public void rebuildIndex(@ApiParam("模型code") @RequestParam(required = false) String model) {
        entitySearchService.rebuildIndex(model);
    }
}
