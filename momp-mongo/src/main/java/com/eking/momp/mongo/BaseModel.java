package com.eking.momp.mongo;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseModel {
	@Id
	private String id;

	@Field("sys_init")
	private Boolean sysInit;

	@Field("create_time")
	private LocalDateTime createTime;

	@Field("create_user")
	private String createUser;

	@Field("update_time")
	private LocalDateTime updateTime;

	@Field("update_user")
	private String updateUser;

	@Field("deleted")
	private LocalDateTime deleted;
}
