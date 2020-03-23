package com.eking.momp.model.service.impl;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.common.exception.InUsedException;
import com.eking.momp.db.mapper.ModelRelationMapper;
import com.eking.momp.db.model.Model;
import com.eking.momp.db.model.ModelRelation;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.dto.*;
import com.eking.momp.model.param.ModelRelationParam;
import com.eking.momp.model.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModelRelationServiceImpl extends AbstractService<ModelRelationMapper, ModelRelation>
        implements ModelRelationService {
    @Autowired
    private ModelService modelService;
    @Autowired
    private RelationTypeService relationTypeService;
    @Autowired
    private ModelFieldService modelFieldService;
    @Autowired
    private EntityRelationService entityRelationService;

    @Override
    public List<ModelRelationDetailDto> listDetailByModelId(Integer modelId) {
        List<ModelRelation> relations = super.listObjByProps(PropBean.of("model_id", modelId));
        return transToDetailDto(relations);
    }

    @Override
    public List<ModelRelationDetailDto> listDetailByTargetModelId(Integer modelId) {
        List<ModelRelation> relations = super.listObjByProps(PropBean.of("target_model_id", modelId));
        return transToDetailDto(relations);
    }

    @Override
    public List<ModelRelationDetailDto> listDetail() {
        List<ModelRelation> relations = super.listObjs();
        return transToDetailDto(relations);
    }

    @Override
    public List<ModelRelationDto> list() {
        List<ModelRelation> relations = super.listObjs();
        return relations.stream().map(ModelRelationDto::new).collect(Collectors.toList());
    }

    @Override
    public List<ModelRelationDto> listByModelId(Integer modelId) {
        List<ModelRelation> relations = super.listObjByProps(PropBean.of("model_id", modelId));
        return relations.stream().map(ModelRelationDto::new).collect(Collectors.toList());
    }

    @Override
    public List<ModelRelationDto> listByTargetModelId(Integer modelId) {
        List<ModelRelation> relations = super.listObjByProps(PropBean.of("target_model_id", modelId));
        return relations.stream().map(ModelRelationDto::new).collect(Collectors.toList());
    }

    @Override
    public ModelRelationDto getById(Integer id) {
        return super.getObjById(id)
                .map(ModelRelationDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelRelation, id));
    }

    @Override
    public ModelRelationDetailDto getDetailById(Integer id) {
        return super.getObjById(id)
                .map(this::transToDetailDto)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelRelation, id));
    }

    @Override
    public ModelRelationDto save(ModelRelationParam param) {
        checkObjExisted("code", PropBean.of("code", param.getCode()));
        checkObjExisted("name", PropBean.of("model_id", param.getModelId()),
                PropBean.of("target_model_id", param.getTargetModelId()), PropBean.of("name", param.getName()));

        ModelRelation relation = new ModelRelation(param.getCode(), param.getName(), param.getRelationTypeId(),
                param.getMapping(), param.getModelId(), param.getTargetModelId(), param.getModelFieldId(),
                param.getDescription());
        ModelRelation returnRelation = super.saveObj(relation);
        Model model = modelService.getForUpdate(relation.getModelId());
        modelService.update(model);
        return new ModelRelationDto(returnRelation);
    }

    @Override
    public ModelRelationDto update(Integer id, ModelRelationParam param) {
        checkObjExisted(id, "code", PropBean.of("code", param.getCode()));
        checkObjExisted(id, "name", PropBean.of("model_id", param.getModelId()),
                PropBean.of("target_model_id", param.getTargetModelId()), PropBean.of("name", param.getName()));

        ModelRelation relation = super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelRelation, id));

        if (Boolean.TRUE.equals(relation.isSysInit())) {
            checkRoleAcl("sysadmin");
        }

        if (param.getCode() != null) {
            relation.setCode(param.getCode());
        }
        if (param.getName() != null) {
            relation.setName(param.getName());
        }
        if (param.getRelationTypeId() != null) {
            relation.setRelationTypeId(param.getRelationTypeId());
        }
        relation.setMapping(param.getMapping());
        if (param.getModelId() != null) {
            relation.setModelId(param.getModelId());
        }
        if (param.getTargetModelId() != null) {
            relation.setTargetModelId(param.getTargetModelId());
        }
        relation.setModelFieldId(param.getModelFieldId());
        relation.setDescription(param.getDescription());
        ModelRelation returnRelation = super.updateObjById(relation);
        Model model = modelService.getForUpdate(relation.getModelId());
        modelService.update(model);
        return new ModelRelationDto(returnRelation);
    }

    @Override
    public void delete(Integer id) {
        ModelRelationDto relation = this.getById(id);
        if (Boolean.TRUE.equals(relation.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        entityRelationService.listByModelRelationId(id)
                .stream()
                .findAny()
                .ifPresent(relationDto -> {
                    ModelRelationDto modelRelation = this.getById(id);
                    throw new InUsedException(ResourceType.ModelRelation, modelRelation.getName(),
                            ResourceType.EntityRelation, relationDto.getId());
                });
        super.deleteObj(id);
    }

    private ModelRelationDetailDto transToDetailDto(ModelRelation relation) {
        ModelRelationDetailDto dto = new ModelRelationDetailDto(relation);
        RelationTypeDto relationType = relationTypeService.getById(dto.getRelationTypeId());
        dto.setRelationType(relationType);
        ModelDto model = modelService.getById(dto.getModelId());
        dto.setModel(model);
        ModelDto targetModel = modelService.getById(dto.getTargetModelId());
        dto.setTargetModel(targetModel);
        if (dto.getModelFieldId() != null) {
            ModelFieldDto modelField = modelFieldService.getById(dto.getModelFieldId());
            dto.setModelField(modelField);
        }
        return dto;
    }

    private List<ModelRelationDetailDto> transToDetailDto(List<ModelRelation> relations) {
        return relations.stream()
                .map(this::transToDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelRelationDto> listByRelationTypeId(Integer relationTypeId) {
        List<ModelRelation> relations = super.listObjByProps(PropBean.of("relation_type_id",
                relationTypeId));
        return relations.stream().map(ModelRelationDto::new).collect(Collectors.toList());
    }
}
