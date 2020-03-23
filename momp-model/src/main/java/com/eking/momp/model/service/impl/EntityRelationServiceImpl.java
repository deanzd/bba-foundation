package com.eking.momp.model.service.impl;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.BusinessException;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.common.param.PageParam;
import com.eking.momp.db.model.ModelRelation;
import com.eking.momp.model.dto.*;
import com.eking.momp.model.param.EntityRelationParam;
import com.eking.momp.model.param.EntityRelationQueryParam;
import com.eking.momp.model.param.EntityRelationUpdateParam;
import com.eking.momp.model.service.EntityRelationService;
import com.eking.momp.model.service.EntityService;
import com.eking.momp.model.service.ModelFieldService;
import com.eking.momp.model.service.ModelRelationService;
import com.eking.momp.mongo.AbstractService;
import com.eking.momp.mongo.model.EntityRelation;
import com.eking.momp.mongo.repository.EntityRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EntityRelationServiceImpl extends AbstractService<EntityRelationRepository, EntityRelation>
        implements EntityRelationService {

    @Autowired
    private ModelRelationService modelRelationService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private ModelFieldService modelFieldService;

    @Override
    public List<EntityRelation> listByEntityIdAndModelRelationIds(String entityId,
                                                                  List<Integer> allowModelRelationIds) {
        return this.repository.findByEntityIdAndModelRelationIdIn(entityId, allowModelRelationIds);
    }

    @Override
    public List<EntityRelation> listByTargetEntityIdAndModelRelationIds(String targetEntityId,
                                                                        List<Integer> allowModelRelationIds) {
        return this.repository.findByTargetEntityIdAndModelRelationIdInAndDeletedIsNull(targetEntityId,
                allowModelRelationIds);
    }

    @Override
    public EntityRelationDto save(EntityRelationParam param) {
        this.checkMapping(null, param.getModelRelationId(), param.getEntityId(), param.getTargetEntityId());

        EntityRelation relation = new EntityRelation();
        relation.setModelRelationId(param.getModelRelationId());
        relation.setEntityModel(param.getEntityModel());
        relation.setEntityId(param.getEntityId());
        relation.setTargetEntityModel(param.getTargetEntityModel());
        relation.setTargetEntityId(param.getTargetEntityId());
        EntityRelation entityRelation = super.saveEntity(relation);
        return new EntityRelationDto(entityRelation);
    }

    @Override
    public EntityRelationDto update(String id, EntityRelationUpdateParam param) {
        EntityRelation relation = super.getEntityById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.EntityRelation, id));
        this.checkMapping(id, relation.getModelRelationId(), param.getEntityId(), param.getTargetEntityId());

        if (param.getEntityId() != null) {
            relation.setEntityId(param.getEntityId());
        }
        if (param.getTargetEntityId() != null) {
            relation.setTargetEntityId(param.getTargetEntityId());
        }
        EntityRelation entityRelation = super.updateEntity(relation);
        return new EntityRelationDto(entityRelation);
    }

    @Override
    public void delete(String... ids) {
        for (String id : ids) {
            super.deleteEntity(id);
        }
    }

    @Override
    public PageBean<EntityRelationDetailDto> page(PageParam<EntityRelationQueryParam> param) {
        int pageIndex = param.getPageIndex();
        int pageSize = param.getPageSize();
        String sortBy = param.getSortBy();
        Boolean asc = param.getAsc();
        EntityRelationQueryParam filter = param.getFilter();

        Criteria criteria = new Criteria();
        if (filter.getEntityId() != null) {
            criteria.and("entity_id").is(filter.getEntityId());
        }
        if (filter.getTargetEntityId() != null) {
            criteria.and("target_entity_id").is(filter.getTargetEntityId());
        }
        if (filter.getModelRelationId() != null) {
            criteria.and("model_relation_id").is(filter.getModelRelationId());
        }
        Page<EntityRelation> page = super.getPage(pageIndex, pageSize, sortBy, asc, criteria);
        List<EntityRelationDetailDto> rows = page.getContent().stream()
                .map(relation -> {
                    ModelRelationDetailDto modelRelation =
                            modelRelationService.getDetailById(relation.getModelRelationId());
                    Map<String, Object> entity = entityService.getById(relation.getEntityModel(),
                            relation.getEntityId());
                    Map<String, Object> targetEntity = entityService.getById(relation.getTargetEntityModel(),
                            relation.getTargetEntityId());

                    List<ModelFiledValueDto> showInListFields = new ArrayList<>();
                    if (filter.getEntityId() != null) {
                        showInListFields = buildShowInListFields(modelRelation.getTargetModel().getId(),
                                modelRelation.getTargetModel().getCode(), relation.getTargetEntityId());
                    } else if (filter.getTargetEntityId() != null) {
                        showInListFields = buildShowInListFields(modelRelation.getModel().getId(),
                                modelRelation.getModel().getCode(), relation.getEntityId());
                    }
                    return new EntityRelationDetailDto(relation.getId(), modelRelation, entity, targetEntity,
                            showInListFields);
                })
                .collect(Collectors.toList());
        return PageBean.of(rows, page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<EntityRelationDto> listByEntityId(String entityId) {
        List<EntityRelation> relations = super.listObjs(PropBean.of("entity_id", entityId));
        return relations.stream()
                .map(EntityRelationDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityRelationDto> listByTargetEntityId(String targetEntityId) {
        List<EntityRelation> relations = super.listObjs(PropBean.of("target_entity_id", targetEntityId));
        return relations.stream()
                .map(EntityRelationDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntityRelationDto> listByModelRelationId(Integer modelRelationId) {
        List<EntityRelation> relations = super.listObjs(PropBean.of("model_relation_id", modelRelationId));
        return relations.stream()
                .map(EntityRelationDto::new)
                .collect(Collectors.toList());
    }

    private List<ModelFiledValueDto> buildShowInListFields(Integer modelId, String modelCode, String entityId) {
        List<ModelFieldDto> fields = modelFieldService.listByModelId(modelId);
        Map<String, Object> entity = entityService.getById(modelCode, entityId);
        return fields.stream()
                .filter(field -> Boolean.TRUE.equals(field.getShowInList()))
                .map(field -> {
                    ModelFiledValueDto fieldValue = new ModelFiledValueDto();
                    fieldValue.setCode(field.getCode());
                    fieldValue.setName(field.getName());
                    fieldValue.setDataType(field.getDataType());
                    fieldValue.setValue(entity.get(field.getCode()));
                    return fieldValue;
                })
                .collect(Collectors.toList());
    }

    private void checkMapping(String thisId, Integer modelRelationId, String entityId, String targetEntityId) {
        ModelRelationDto modelRelation = modelRelationService.getById(modelRelationId);
        ModelRelation.Mapping mapping = modelRelation.getMapping();
        if (mapping == null) {
            return;
        }
        switch (mapping) {
            case OneToOne:
                checkMappingInternal(thisId,
                        localeService.getMessage("modelRelationMappingConflict", mapping.getText()),
                        PropBean.of("model_relation_id", modelRelationId),
                        PropBean.of("target_entity_id", targetEntityId));
                checkMappingInternal(thisId,
                        localeService.getMessage("modelRelationMappingConflict", mapping.getText()),
                        PropBean.of("model_relation_id", modelRelationId),
                        PropBean.of("entity_id", entityId));
                break;
            case OneToMany:
                checkMappingInternal(thisId,
                        localeService.getMessage("modelRelationMappingConflict", mapping.getText()),
                        PropBean.of("model_relation_id", modelRelationId),
                        PropBean.of("target_entity_id", targetEntityId));
                break;
            case ManyToOne:
                checkMappingInternal(thisId,
                        localeService.getMessage("modelRelationMappingConflict", mapping.getText()),
                        PropBean.of("model_relation_id", modelRelationId),
                        PropBean.of("entity_id", entityId));
                break;
            case ManyToMany:
                checkMappingInternal(thisId,
                        localeService.getMessage("alreadyExists", "关联标识"),
                        PropBean.of("model_relation_id", modelRelationId),
                        PropBean.of("entity_id", entityId),
                        PropBean.of("target_entity_id", targetEntityId));
                break;
        }
    }

    private void checkMappingInternal(String thisId, String message, PropBean... props) {
        super.listObjs(props).stream()
                .filter(entityRelation -> thisId == null || !thisId.equals(entityRelation.getId()))
                .findAny()
                .ifPresent(er -> {
                    throw new BusinessException(message);
                });
    }

}
