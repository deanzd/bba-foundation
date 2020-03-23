package com.eking.momp.org.param;

import lombok.Data;

@Data
public class UserParam {
    private String username;
    private String password;
    private Integer roleId;
}
