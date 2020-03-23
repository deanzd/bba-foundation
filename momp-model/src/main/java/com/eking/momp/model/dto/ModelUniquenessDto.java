package com.eking.momp.model.dto;

import java.util.List;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.ModelUniqueness;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelUniquenessDto extends BaseDto {

    private static final long serialVersionUID = -2652468524176793444L;
    private Integer modelId;
	
	private Boolean required;

	private List<ModelUniquenessItemDto> items;

	public ModelUniquenessDto(ModelUniqueness model) {
		super(model);
		this.modelId = model.getModelId();
		this.required = model.getRequired();
	}

}
