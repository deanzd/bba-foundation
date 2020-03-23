package com.eking.momp.model.dto;

import com.eking.momp.db.model.ModelRelation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelRelationDetailDto extends ModelRelationDto {

    private static final long serialVersionUID = 1138713190432303870L;
    private RelationTypeDto relationType;

	private ModelDto model;

	private ModelDto targetModel;

	private ModelFieldDto modelField;

	public ModelRelationDetailDto(ModelRelation relation) {
		super(relation);
	}
}
