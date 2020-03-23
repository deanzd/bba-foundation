package com.eking.momp.org.service;

import com.eking.momp.org.dto.RoleDto;

import java.util.List;

public interface RoleService {
    RoleDto getById(Integer id);
    List<RoleDto> list();
}
