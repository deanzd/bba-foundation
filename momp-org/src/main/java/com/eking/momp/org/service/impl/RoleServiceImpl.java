package com.eking.momp.org.service.impl;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.mapper.RoleMapper;
import com.eking.momp.db.model.Role;
import com.eking.momp.db.AbstractService;
import com.eking.momp.org.dto.RoleDto;
import com.eking.momp.org.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends AbstractService<RoleMapper, Role> implements RoleService {

    @Override
    public RoleDto getById(Integer id) {
        return super.getObjById(id)
                .map(RoleDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Role, id));
    }

    @Override
    public List<RoleDto> list() {
        return super.listObjs().stream().map(RoleDto::new).collect(Collectors.toList());
    }
}
