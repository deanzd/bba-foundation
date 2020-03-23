package com.eking.momp.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "relation type", description = "关系类型")
public class RelationTypeParam {
	
	@ApiModelProperty("唯一标识")
	private String code;

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("源->目标")
	private String text;

	@ApiModelProperty("目标->源")
	private String reverseText;
}
