package com.eking.momp.common.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("分页参数")
public class PageParam<T> {
    @ApiModelProperty(value = "起始页索引", required = true)
    private int pageIndex;
    @ApiModelProperty(value = "页大小", required = true)
    private int pageSize;
    @ApiModelProperty("排序字段")
    private String sortBy;
    @ApiModelProperty("正序排序")
    private Boolean asc;
    @ApiModelProperty("搜索关键字")
    private String keyword;
    @ApiModelProperty("过滤条件")
    private T filter;
}
