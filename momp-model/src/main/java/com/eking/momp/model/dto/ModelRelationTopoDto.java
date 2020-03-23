package com.eking.momp.model.dto;

import com.eking.momp.db.model.ModelRelation.Mapping;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ModelRelationTopoDto implements Serializable {

    private static final long serialVersionUID = 4550816447224464815L;
    private Integer id;
	
	private String code;
	
	private String name;

	private Integer relationTypeId;

	private Mapping mapping;

	private String modelId;//处理后的ID

	private String targetModelId;//处理后的ID
	
	private Integer modelFieldId;
	
	private String description; 

	private RelationTypeDto relationType;
	
	private ModelTopoDto model;

	private ModelTopoDto targetModel;

	private ModelFieldDto modelField;

}
