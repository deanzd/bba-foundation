package com.eking.momp.db;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseDto implements Serializable {

	private static final long serialVersionUID = -2235687455247986067L;
	@ApiModelProperty("ID")
	private Integer id;
	
	@ApiModelProperty("是否内置")
    private Boolean sysInit;

	@ApiModelProperty("创建时间")
    private LocalDateTime createTime;

	@ApiModelProperty("创建人")
    private String createUser;

	@ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

	@ApiModelProperty("更新人")
    private String updateUser;

	protected BaseDto(BaseModel<?> model) {
		this.id = model.getId();
		this.sysInit = model.isSysInit();
		this.createTime = model.getCreateTime();
		this.createUser = model.getCreateUser();
		this.updateTime = model.getUpdateTime();
		this.updateUser = model.getUpdateUser();
	}
}
