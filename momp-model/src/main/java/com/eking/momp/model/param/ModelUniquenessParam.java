package com.eking.momp.model.param;

import java.util.List;

import lombok.Data;

@Data
public class ModelUniquenessParam {

	private Integer modelId;

	private Boolean required;
	
	private List<ModelUniquenessItemParam> items;
}
