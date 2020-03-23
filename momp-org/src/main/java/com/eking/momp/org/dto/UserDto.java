package com.eking.momp.org.dto;

import com.eking.momp.db.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class UserDto {

    private Integer id;
    private String username;
    @JsonIgnore
    private String password;
    private Integer roleId;
    private RoleDto role;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.roleId = user.getRoleId();
    }
}
