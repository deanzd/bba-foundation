package com.eking.momp.controller;

import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.param.PageParam;
import com.eking.momp.model.dto.EntityRelationDetailDto;
import com.eking.momp.model.dto.EntityRelationDto;
import com.eking.momp.model.param.EntityRelationParam;
import com.eking.momp.model.param.EntityRelationQueryParam;
import com.eking.momp.model.param.EntityRelationUpdateParam;
import com.eking.momp.model.service.EntityRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/entity-relations")
@Api(tags = "实例关系")
public class EntityRelationController {
    @Autowired
    private EntityRelationService entityRelationService;

    @PostMapping("/page")
    @ApiOperation("获取实例关系列表")
    public PageBean<EntityRelationDetailDto> listRelations(
            @ApiParam("过滤条件") @RequestBody PageParam<EntityRelationQueryParam> pageParam) {
        return entityRelationService.page(pageParam);
    }

    @PostMapping("")
    @ApiOperation("创建实例关系")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin', 'operator')")
    public EntityRelationDto saveRelation(
            @ApiParam("参数") @RequestBody EntityRelationParam param) {
        return entityRelationService.save(param);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新实例关系")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin', 'operator')")
    public EntityRelationDto updateRelation(
            @ApiParam("实例关系ID") @PathVariable String id,
            @ApiParam("参数") @RequestBody EntityRelationUpdateParam param) {
        return entityRelationService.update(id, param);
    }

    @DeleteMapping("/{ids}")
    @ApiOperation("删除实例关系")
    @PreAuthorize("hasAnyRole('sysadmin', 'admin', 'operator')")
    public String[] deleteRelation(@ApiParam("实例关系ID") @PathVariable String... ids) {
        entityRelationService.delete(ids);
        return ids;
    }
}
