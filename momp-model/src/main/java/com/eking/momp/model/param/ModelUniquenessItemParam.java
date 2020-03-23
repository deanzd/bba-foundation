package com.eking.momp.model.param;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ModelUniquenessItemParam {
	
	@JsonIgnore
	private Integer modelUniquenessId;

	private Integer modelFieldId;

}
