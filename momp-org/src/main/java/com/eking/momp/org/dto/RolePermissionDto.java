package com.eking.momp.org.dto;

import com.eking.momp.db.model.RolePermission;
import lombok.Data;

@Data
public class RolePermissionDto {

    private Integer roleId;

    private Integer permissionId;

    public RolePermissionDto(RolePermission rolePermission) {
        this.roleId = rolePermission.getRoleId();
        this.permissionId = rolePermission.getPermissionId();
    }
}
