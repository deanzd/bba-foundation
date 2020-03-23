package com.eking.momp.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "layer", description = "资源层参数")
public class LayerParam {

	@ApiModelProperty("唯一标识")
	private String code;

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("备注")
	private String description;

	@ApiModelProperty("图标编码")
	private String iconImage;

	@ApiModelProperty("显示顺序")
	private Integer showOrder;

	@ApiModelProperty("是否内置")
	private Boolean sysInit;
}
