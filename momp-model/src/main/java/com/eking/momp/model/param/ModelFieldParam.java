package com.eking.momp.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "field", description = "字段参数")
public class ModelFieldParam {

	@ApiModelProperty("模型ID")
	private Integer modelId;

	@ApiModelProperty("唯一标识")
	private String code;

	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("备注")
	private String description;

	@ApiModelProperty("是否必填")
	private Boolean required;

	@ApiModelProperty("数据类型")
	private String dataType;

	@ApiModelProperty("正则")
	private String verifyRegex;

	@ApiModelProperty("是否列表展示")
	private Boolean showInTable;

	@ApiModelProperty("是否概要展示")
	private Boolean showInList;
	
	@ApiModelProperty("是否是搜索关键字")
	private Boolean searchKey;

	@ApiModelProperty("显示顺序")
	private Integer showOrder;
}
