package com.eking.momp.controller;

import com.eking.momp.org.dto.RoleDto;
import com.eking.momp.org.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@Api(tags = "角色")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("")
    @ApiOperation("获取角色列表")
    public List<RoleDto> list() {
        return roleService.list();
    }
}
