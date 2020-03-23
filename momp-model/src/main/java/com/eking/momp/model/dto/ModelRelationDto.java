package com.eking.momp.model.dto;

import com.eking.momp.db.BaseDto;
import com.eking.momp.db.model.ModelRelation;
import com.eking.momp.db.model.ModelRelation.Mapping;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelRelationDto extends BaseDto {

    private static final long serialVersionUID = -4079723782138659830L;
    private String code;
	
	private String name;

	private Integer relationTypeId;

	private Mapping mapping;

	private Integer modelId;

	private Integer targetModelId;
	
	private Integer modelFieldId;
	
	private String description; 

	public ModelRelationDto(ModelRelation relation) {
		super(relation);
		this.code = relation.getCode();
		this.name = relation.getName();
		this.relationTypeId = relation.getRelationTypeId();
		this.mapping = relation.getMapping();
		this.modelId = relation.getModelId();
		this.targetModelId = relation.getTargetModelId();
		this.modelFieldId = relation.getModelFieldId();
		this.description = relation.getDescription();
	}

}
