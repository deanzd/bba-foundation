package com.eking.momp.model.dto;

import java.util.List;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.Layer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LayerDto extends BaseDto {

    private static final long serialVersionUID = 765200259072401812L;
    private String code;

	private String name;

	private String description;

	private Integer showOrder;

	private String iconImage;

	private List<ModelDto> models;

	public LayerDto(Layer layer) {
		super(layer);
		this.code = layer.getCode();
		this.name = layer.getName();
		this.description = layer.getDescription();
		this.showOrder = layer.getShowOrder();
		this.iconImage = layer.getIconImage();
	}
}
