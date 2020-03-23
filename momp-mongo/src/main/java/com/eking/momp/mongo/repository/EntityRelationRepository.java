package com.eking.momp.mongo.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.eking.momp.mongo.model.EntityRelation;

@Repository
public interface EntityRelationRepository extends MongoRepository<EntityRelation, String> {

	List<EntityRelation> findByEntityIdAndModelRelationIdIn(String entityId, List<Integer> modelRelationIds);

	List<EntityRelation> findByTargetEntityIdAndModelRelationIdInAndDeletedIsNull(String targetEntityId,
			List<Integer> modelRelationIds);
}
