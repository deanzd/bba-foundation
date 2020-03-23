package com.eking.momp.model.param;

import com.eking.momp.db.model.ModelRelation.Mapping;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "model relation", description = "模型关系参数")
public class ModelRelationParam {
	
	@ApiModelProperty("关联唯一标识")
	private String code;
	
	@ApiModelProperty("关联名称")
	private String name;
	
	@ApiModelProperty("关系类型ID")
	private Integer relationTypeId;

	@ApiModelProperty("映射关系")
	private Mapping mapping;

	@ApiModelProperty("源模型ID")
	private Integer modelId;

	@ApiModelProperty("目标模型ID")
	private Integer targetModelId;
	
	@ApiModelProperty("源关联字段ID")
	private Integer modelFieldId;
	
	@ApiModelProperty("备注")
	private String description;
	
}
