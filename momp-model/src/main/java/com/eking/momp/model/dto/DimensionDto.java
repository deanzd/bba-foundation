package com.eking.momp.model.dto;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.Dimension;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DimensionDto extends BaseDto {

	private static final long serialVersionUID = 8552464827962661183L;
	private String name;

	private String description;

	private Integer modelId;

	private Integer showOrder;
	
	public DimensionDto(Dimension dimension) {
		super(dimension);
		this.name = dimension.getName();
		this.description = dimension.getDescription();
		this.modelId = dimension.getModelId();
		this.showOrder = dimension.getShowOrder();
	}
}
