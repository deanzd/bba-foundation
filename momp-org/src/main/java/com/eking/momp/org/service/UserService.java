package com.eking.momp.org.service;

import java.util.List;

import com.eking.momp.db.model.User;
import com.eking.momp.org.dto.UserDto;
import com.eking.momp.org.param.UserParam;

public interface UserService {
	UserDto getById(Integer id);
	UserDto getByUsername(String username);
	UserDto save(UserParam userParam);
	User delete(Integer id);
	UserDto update(Integer id, UserParam userParam);
    List<UserDto> list();
}
