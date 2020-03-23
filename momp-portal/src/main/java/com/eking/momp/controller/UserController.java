package com.eking.momp.controller;

import com.eking.momp.org.dto.UserDto;
import com.eking.momp.org.param.UserParam;
import com.eking.momp.org.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Api(tags = "用户")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @ApiOperation("查询用户")
    public UserDto findById(@ApiParam("用户ID") @PathVariable Integer id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    public Integer delete(@ApiParam("用户ID") @PathVariable Integer id) {
        userService.delete(id);
        return id;
    }

    @PostMapping("")
    @ApiOperation("添加用户")
    public UserDto save(@ApiParam("用户属性") @RequestBody UserParam userParam) {
        return userService.save(userParam);
    }

    @GetMapping("")
    @ApiOperation("获取用户列表")
    public List<UserDto> list() {
        return userService.list();
    }

    @PutMapping("/{id}")
    @ApiOperation("更新用户")
    public UserDto updateUser(@ApiParam("用户ID") @PathVariable Integer id,
                              @ApiParam("用户属性") @RequestBody UserParam userParam) {
        return userService.update(id, userParam);
    }

}
