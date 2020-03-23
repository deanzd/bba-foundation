package com.eking.momp.org.dto;

import com.eking.momp.db.model.Permission;
import lombok.Data;

@Data
public class PermissionDto {

    private String code;

    private String name;

    private String description;

    public PermissionDto(Permission permission) {
        this.code = permission.getCode();
        this.name = permission.getName();
        this.description = permission.getDescription();
    }
}
