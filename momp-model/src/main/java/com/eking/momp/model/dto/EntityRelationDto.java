package com.eking.momp.model.dto;



import com.eking.momp.mongo.model.EntityRelation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class EntityRelationDto implements Serializable {

    private static final long serialVersionUID = 3559172021028294517L;
    private String id;

	private Integer modelRelationId;
	
	private String entityModel;

	private String entityId;

	private String targetEntityModel;

	private String targetEntityId;
	
	private RelationTypeDto relationType;
	
	public EntityRelationDto(EntityRelation relation) {
		this.id = relation.getId();
		this.modelRelationId = relation.getModelRelationId();
		this.entityModel = relation.getEntityModel();
		this.entityId = relation.getEntityId();
		this.targetEntityModel = relation.getTargetEntityModel();
		this.targetEntityId = relation.getTargetEntityId();
	}
}
