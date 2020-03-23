package com.eking.momp.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class EntityDto {
    private String id;
    private ModelDto model;
    private List<ModelFiledValueDto> fields;
}
