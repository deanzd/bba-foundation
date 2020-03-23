package com.eking.momp.org.dto;

import com.eking.momp.db.model.Role;
import lombok.Data;

@Data
public class RoleDto {

    private Integer id;

    private String name;

    private String code;

    private String description;

    public RoleDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.code = role.getCode();
        this.description = role.getDescription();
    }
}
