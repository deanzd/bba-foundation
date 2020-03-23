package com.eking.momp.model.dto;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.Model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelDto extends BaseDto {

    private static final long serialVersionUID = -1887490576667363885L;
    private String code;

    private String name;

    private String description;

    private String iconImage;
    
    private Integer layerId;
    
    private Integer showOrder;

    private LayerDto layerDto;
    
    public ModelDto(Model model) {
    	super(model);
    	this.code = model.getCode();
    	this.name = model.getName();
    	this.description = model.getDescription();
    	this.iconImage = model.getIconImage();
    	this.layerId = model.getLayerId();
    	this.showOrder = model.getShowOrder();
    }
}
