package com.eking.momp.org.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.common.Constant;
import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.mapper.UserMapper;
import com.eking.momp.db.model.User;
import com.eking.momp.db.AbstractService;
import com.eking.momp.org.dto.RoleDto;
import com.eking.momp.org.dto.UserDto;
import com.eking.momp.org.param.UserParam;
import com.eking.momp.org.service.RoleService;
import com.eking.momp.org.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserServiceImpl extends AbstractService<UserMapper, User> implements UserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
    private RoleService roleService;

    @Override
    public UserDto getById(Integer id) {
        return super.getObjById(id)
                .map(UserDto::new)
                .map(user -> {
                    RoleDto role = roleService.getById(user.getRoleId());
                    user.setRole(role);
                    return user;
                })
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.User, id));
    }

    @Override
    public UserDto getByUsername(String username) {
        QueryWrapper<User> qw;
        qw = new QueryWrapper<>();
        qw.eq("username", username);
        return super.getOneObj(qw)
                .map(UserDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.User, username));
    }

    @Override
    public UserDto save(UserParam userParam) {
        String username = userParam.getUsername();
        String password = userParam.getPassword();
        Integer roleId = userParam.getRoleId();

        checkObjExisted("username", PropBean.of("username", username));

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoleId(roleId);
        User returnUser = super.saveObj(user);
        return new UserDto(returnUser);
    }

    @Override
    public User delete(Integer id) {
        return super.deleteObj(id)
                .filter(editableUser())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.User, id));
    }

    @Override
    public UserDto update(Integer id, UserParam userParam) {
        String password = userParam.getPassword();
        Integer roleId = userParam.getRoleId();

        User user = super.getObjById(id)
                .filter(editableUser())
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.User, id));
//        user.setUsername(userParam.getUsername());
        if (!StringUtils.isEmpty(userParam.getPassword())) {
            user.setPassword(passwordEncoder.encode(password));
        }
        if (userParam.getRoleId() != null) {
            user.setRoleId(roleId);
        }
        User returnUser = super.updateObjById(user);
        return new UserDto(returnUser);
    }

    @Override
    public List<UserDto> list() {
        return super.listObjs().stream()
                .filter(editableUser())
                .map(UserDto::new)
                .peek(user -> user.setRole(roleService.getById(user.getRoleId())))
                .collect(Collectors.toList());
    }

    private Predicate<User> editableUser() {
        return u -> !Constant.SUPERADMIN.equals(u.getUsername());
    }
}
