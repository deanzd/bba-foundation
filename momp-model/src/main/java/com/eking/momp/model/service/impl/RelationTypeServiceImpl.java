package com.eking.momp.model.service.impl;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.common.exception.InUsedException;
import com.eking.momp.db.mapper.RelationTypeMapper;
import com.eking.momp.db.model.RelationType;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.dto.RelationTypeDto;
import com.eking.momp.model.param.RelationTypeParam;
import com.eking.momp.model.service.ModelRelationService;
import com.eking.momp.model.service.RelationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RelationTypeServiceImpl extends AbstractService<RelationTypeMapper, RelationType>
        implements RelationTypeService {

    @Autowired
    private ModelRelationService modelRelationService;

    @Override
    public List<RelationTypeDto> list() {
        List<RelationType> types = super.listObjs();
        List<RelationTypeDto> result = new ArrayList<>();
        types.forEach(type -> result.add(new RelationTypeDto(type)));
        return result;
    }

    @Override
    public RelationTypeDto getById(Integer id) {
        return super.getObjById(id)
                .map(RelationTypeDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.RelationType, id));
    }

    @Override
    public RelationTypeDto save(RelationTypeParam param) {
        checkObjExisted("code", PropBean.of("code", param.getCode()));
        checkObjExisted("name", PropBean.of("name", param.getName()));

        RelationType type = new RelationType();
        type.setCode(param.getCode());
        type.setName(param.getName());
        type.setText(param.getText());
        type.setReverseText(param.getReverseText());
        RelationType returnType = super.saveObj(type);
        return new RelationTypeDto(returnType);
    }

    @Override
    public RelationTypeDto update(Integer id, RelationTypeParam param) {
        checkObjExisted(id, "code", PropBean.of("code", param.getCode()));
        checkObjExisted(id, "name", PropBean.of("name", param.getName()));
        checkObjExisted(id, "relationType", PropBean.of("text", param.getText()));
        checkObjExisted(id, "relationType", PropBean.of("reverse_text", param.getReverseText()));

        RelationType type = super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.RelationType, id));

        if (Boolean.TRUE.equals(type.isSysInit())) {
            checkRoleAcl("sysadmin");
        }

        if (param.getCode() != null) {
            type.setCode(param.getCode());
        }
        if (param.getName() != null) {
            type.setName(param.getName());
        }
        type.setText(param.getText());
        type.setReverseText(param.getReverseText());
        RelationType returnType = super.updateObjById(type);
        return new RelationTypeDto(returnType);
    }

    @Override
    public void delete(Integer id) {
        RelationTypeDto relationType = this.getById(id);

        if (Boolean.TRUE.equals(relationType.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        modelRelationService.listByRelationTypeId(id).stream()
                .findAny()
                .ifPresent(modelRelation -> {
                    throw new InUsedException(ResourceType.RelationType, relationType.getName(),
                            ResourceType.ModelRelation, modelRelation.getName());
                });
        super.deleteObj(id);
    }
}
