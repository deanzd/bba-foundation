package com.eking.momp.org.service.impl;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.mapper.PermissionMapper;
import com.eking.momp.db.model.Permission;
import com.eking.momp.db.AbstractService;
import com.eking.momp.org.dto.PermissionDto;
import com.eking.momp.org.service.PermissionService;
import com.eking.momp.org.service.RolePermissionService;
import com.eking.momp.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends AbstractService<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionService rolePermissionService;
    @Autowired
    private UserService userService;

    @Override
    public List<PermissionDto> listByUserId(Integer userId) {
        Integer roleId = userService.getById(userId).getRoleId();
        return this.listByRoleId(roleId);
    }

    @Override
    public List<PermissionDto> listByRoleId(Integer roleId) {
        return rolePermissionService.listByRoleId(roleId).stream()
                .map(rolePerm -> super.getObjById(rolePerm.getPermissionId()).orElseThrow(() ->
                        new ResourceNotFoundException(ResourceType.Permission, rolePerm.getPermissionId())))
                .map(PermissionDto::new)
                .collect(Collectors.toList());
    }
}
