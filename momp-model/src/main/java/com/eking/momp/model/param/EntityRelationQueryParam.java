package com.eking.momp.model.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("实例关系查询参数")
public class EntityRelationQueryParam {

    @ApiModelProperty("模型关系ID")
    private Integer modelRelationId;

    @ApiModelProperty("源实例ID")
    private String entityId;

    @ApiModelProperty("目标实例ID")
    private String targetEntityId;
}
