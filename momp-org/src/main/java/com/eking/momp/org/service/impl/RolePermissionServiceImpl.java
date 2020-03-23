package com.eking.momp.org.service.impl;

import com.eking.momp.common.bean.PropBean;
import com.eking.momp.db.mapper.RolePermissionMapper;
import com.eking.momp.db.model.RolePermission;
import com.eking.momp.db.AbstractService;
import com.eking.momp.org.dto.RolePermissionDto;
import com.eking.momp.org.service.RolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionServiceImpl extends AbstractService<RolePermissionMapper, RolePermission>
        implements RolePermissionService {
    @Override
    public List<RolePermissionDto> listByRoleId(Integer roleId) {
        return super.listObjByProps(PropBean.of("role_id", roleId)).stream()
                .map(RolePermissionDto::new)
                .collect(Collectors.toList());
    }
}
