package com.eking.momp.model.dto;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.RelationType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RelationTypeDto extends BaseDto {

    private static final long serialVersionUID = -3281865357176937837L;
    private String code;

	private String name;
	
	private String text;

	private String reverseText;

	public RelationTypeDto(RelationType type) {
		super(type);
		this.code = type.getCode();
		this.name = type.getName();
		this.text = type.getText();
		this.reverseText = type.getReverseText();
	}
}
