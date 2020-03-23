package com.eking.momp.model.dto;

import lombok.Data;

@Data
public class ModelFiledValueDto {
    private String code;

    private String name;

    private String dataType;

    private Boolean showInList;

    private Object value;

}
