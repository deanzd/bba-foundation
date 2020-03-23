package com.eking.momp.config;

import java.util.ArrayList;
import java.util.List;

import com.eking.momp.org.dto.RoleDto;
import com.eking.momp.org.dto.UserDto;
import com.eking.momp.org.service.PermissionService;
import com.eking.momp.org.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.eking.momp.common.UserContext;
import com.eking.momp.org.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("登录用户：{}", username);
        // TODO 从数据库中获取，根据用户名，查找到对应的密码，与权限
        UserDto user = userService.getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("username not found");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        RoleDto role = roleService.getById(user.getRoleId());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getCode()));
        permissionService.listByUserId(user.getId())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getCode())));
        return new UserContext(username, user.getPassword(), authorities);
    }

}
