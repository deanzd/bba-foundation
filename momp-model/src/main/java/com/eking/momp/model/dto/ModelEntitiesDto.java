package com.eking.momp.model.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelEntitiesDto implements Serializable {
    private static final long serialVersionUID = -7369611846546751029L;
    private Integer id;

	private String code;

	private String name;

	private String description;

	private String iconImage;

	private Integer layerId;

	private Integer showOrder;

	private List<ModelFieldDto> fields;

	private List<Map<String, Object>> entities;

	public ModelEntitiesDto(ModelDto model) {
		this.id = model.getId();
		this.code = model.getCode();
		this.name = model.getName();
		this.description = model.getDescription();
		this.iconImage = model.getIconImage();
		this.layerId = model.getLayerId();
		this.showOrder = model.getShowOrder();
	}
	
	
}
