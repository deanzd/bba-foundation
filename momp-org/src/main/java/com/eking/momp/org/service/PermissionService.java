package com.eking.momp.org.service;

import com.eking.momp.org.dto.PermissionDto;

import java.util.List;

public interface PermissionService {
    List<PermissionDto> listByUserId(Integer userId);
    List<PermissionDto> listByRoleId(Integer roleId);
}
