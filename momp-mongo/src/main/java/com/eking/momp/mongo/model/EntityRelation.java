package com.eking.momp.mongo.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.eking.momp.mongo.BaseModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document("entity_relation")
public class EntityRelation extends BaseModel {
	
	@Field("model_relation_id")
	private Integer modelRelationId;

	@Field("entity_model")
	private String entityModel;

	@Field("entity_id")
	private String entityId;

	@Field("target_entity_model")
	private String targetEntityModel;

	@Field("target_entity_id")
	private String targetEntityId;
}
