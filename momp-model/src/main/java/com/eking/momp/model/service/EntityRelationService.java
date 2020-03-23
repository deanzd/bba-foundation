package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.param.PageParam;
import com.eking.momp.model.dto.EntityRelationDetailDto;
import com.eking.momp.model.dto.EntityRelationDto;
import com.eking.momp.model.param.EntityRelationParam;
import com.eking.momp.model.param.EntityRelationQueryParam;
import com.eking.momp.model.param.EntityRelationUpdateParam;
import com.eking.momp.mongo.model.EntityRelation;

public interface EntityRelationService {

    List<EntityRelation> listByEntityIdAndModelRelationIds(String entityId, List<Integer> allowModelRelationIds);

    List<EntityRelation> listByTargetEntityIdAndModelRelationIds(String targetEntityId,
                                                                 List<Integer> allowModelRelationIds);

    EntityRelationDto save(EntityRelationParam param);

    EntityRelationDto update(String id, EntityRelationUpdateParam param);

    void delete(String... ids);

    PageBean<EntityRelationDetailDto> page(PageParam<EntityRelationQueryParam> param);

    List<EntityRelationDto> listByEntityId(String entityId);

    List<EntityRelationDto> listByTargetEntityId(String targetEntityId);

    List<EntityRelationDto> listByModelRelationId(Integer modelRelationId);
}
