package com.eking.momp.org.service;

import com.eking.momp.org.dto.RolePermissionDto;

import java.util.List;

public interface RolePermissionService {
    List<RolePermissionDto> listByRoleId(Integer roleId);
}
