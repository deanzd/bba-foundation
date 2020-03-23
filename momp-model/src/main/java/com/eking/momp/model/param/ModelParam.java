package com.eking.momp.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

@ApiModel(value = "model", description = "模型参数")
@Data
public class ModelParam {
	
	@ApiModelProperty("唯一标识")
	@Pattern(regexp = "^[a-z][0-9a-z_\\-]*$", message = "模型编码小写字母、数字、-、_组成，字母开头")
    private String code;

	@ApiModelProperty("名称")
    private String name;

	@ApiModelProperty("备注")
    private String description;

	@ApiModelProperty("图标编码")
    private String iconImage;

	@ApiModelProperty("资源层ID")
    private Integer layerId;

	@ApiModelProperty("显示顺序")
    private Integer showOrder;

	@ApiModelProperty("是否内置")
	private Boolean sysInit;
}
