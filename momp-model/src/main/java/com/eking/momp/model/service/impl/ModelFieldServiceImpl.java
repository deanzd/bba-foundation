package com.eking.momp.model.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.common.Constant;
import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.BusinessException;
import com.eking.momp.common.exception.InUsedException;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.AbstractService;
import com.eking.momp.db.mapper.ModelFieldMapper;
import com.eking.momp.db.model.Model;
import com.eking.momp.db.model.ModelField;
import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.dto.ModelFieldDto;
import com.eking.momp.model.param.ModelFieldParam;
import com.eking.momp.model.service.EntityService;
import com.eking.momp.model.service.ModelFieldService;
import com.eking.momp.model.service.ModelService;
import com.eking.momp.model.service.ModelUniquenessItemService;
import com.eking.momp.model.stream.EntitySearchSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModelFieldServiceImpl extends AbstractService<ModelFieldMapper, ModelField> implements ModelFieldService {

    @Autowired
    private EntityService entityService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ModelUniquenessItemService modelUniquenessItemService;
    @Autowired
    private EntitySearchSender entitySearchSender;

    @Override
    public List<ModelFieldDto> listByModelId(Integer modelId) {
        List<ModelField> modelFields = super.listObjByProps(Constant.FIELD_SHOW_ORDER, true,
                PropBean.of("model_id", modelId));
        return modelFields.stream()
                .map(ModelFieldDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public ModelFieldDto getById(Integer id) {
        return super.getObjById(id)
                .map(ModelFieldDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelField, id));
    }

    @Override
    public ModelFieldDto save(ModelFieldParam param) {
        ModelDto modelDto = modelService.getById(param.getModelId());
        if (Boolean.TRUE.equals(modelDto.getSysInit())) {
            checkRoleAcl("sysadmin");
        }
        checkObjExisted("fieldCode", PropBean.of("code", param.getCode()));
        checkObjExisted("fieldName", PropBean.of("model_id", param.getModelId()),
                PropBean.of("name", param.getName()));

        ModelField field = new ModelField();
        field.setModelId(param.getModelId());
        field.setCode(param.getCode());
        field.setName(param.getName());
        field.setDescription(param.getDescription());
        field.setRequired(param.getRequired());
        field.setDataType(param.getDataType());
        field.setVerifyRegex(param.getVerifyRegex());
        field.setShowInTable(param.getShowInTable());
        field.setShowInList(param.getShowInList());
        field.setSearchKey(param.getSearchKey());
        field.setShowOrder(param.getShowOrder());
        ModelField returnField = super.saveObj(field);
        this.checkShowInListCount(returnField.getModelId());
        Model model = modelService.getForUpdate(field.getModelId());
        modelService.update(model);
        return new ModelFieldDto(returnField);
    }

    @Override
    public ModelFieldDto update(Integer fieldId, ModelFieldParam param) {
        ModelDto model = modelService.getById(param.getModelId());
        if (Boolean.TRUE.equals(model.getSysInit())) {
            checkRoleAcl("sysadmin");
        }
        checkObjExisted(fieldId, "fieldCode", PropBean.of("code", param.getCode()));
        checkObjExisted(fieldId, "fieldName", PropBean.of("model_id", param.getModelId()),
                PropBean.of("name", param.getName()));

        ModelField field = super.getObjById(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelField, fieldId));
        if (param.getCode() != null) {
            field.setCode(param.getCode());
        }
        if (param.getName() != null) {
            field.setName(param.getName());
        }
        field.setDescription(param.getDescription());
        if (param.getRequired() != null) {
            field.setRequired(param.getRequired());
        }
        if (param.getDataType() != null) {
            field.setDataType(param.getDataType());
        }
        if (param.getShowInTable() != null) {
            field.setShowInTable(param.getShowInTable());
        }
        if (param.getShowInList() != null) {
            field.setShowInList(param.getShowInList());
        }
        boolean updateSearchKey = false;
        if (param.getSearchKey() != null) {
            boolean b1 = field.getSearchKey() != null ? field.getSearchKey() : false;
            boolean b2 = param.getSearchKey() != null ? param.getSearchKey() : false;
            if (b1 != b2) {
                field.setSearchKey(param.getSearchKey());
                updateSearchKey = true;
            }
        }
        field.setShowOrder(param.getShowOrder());
        field.setVerifyRegex(param.getVerifyRegex());
        ModelField returnField = super.updateObjById(field);

        this.checkShowInListCount(returnField.getModelId());

        Model modelForUpdate = modelService.getForUpdate(field.getModelId());
        modelService.update(modelForUpdate);//需要更新model的更新时间

        if (updateSearchKey) {
            entitySearchSender.updateModel(model.getCode());
        }

        return new ModelFieldDto(returnField);
    }

    public void delete(Integer id) {
        ModelField field = super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelField, id));
        ModelDto model = modelService.getById(field.getModelId());

        if (Boolean.TRUE.equals(model.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        // 检查字段在实例中是否被使用
        Criteria criteria = Criteria.where(field.getCode()).ne(null);
        Query query = Query.query(criteria);
        entityService.list(model.getCode(), query).stream()
                .findFirst()
                .ifPresent(entity -> {
                    throw new InUsedException(ResourceType.ModelField, field.getName(),
                            ResourceType.Entity, entity.get("id").toString());
                });
        // 检查是否在唯一性约束item中使用
        modelUniquenessItemService.listByModelFieldId(id)
                .stream()
                .findAny()
                .ifPresent(uniqItem -> {
                    throw new InUsedException(ResourceType.ModelField, field.getName(),
                            ResourceType.Uniqueness, uniqItem.getModelUniquenessId());
                });
        super.deleteObj(id);
    }

    @Override
    public List<ModelFieldDto> list(QueryWrapper<ModelField> qw) {
        List<ModelField> fields = super.listObjs(qw);
        List<ModelFieldDto> result = new ArrayList<>();
        fields.forEach(field -> result.add(new ModelFieldDto(field)));
        return result;
    }

    @Override
    public ModelFieldDto getByCode(Integer modelId, String code) {
        QueryWrapper<ModelField> qw = new QueryWrapper<>();
        qw.eq("model_id", modelId);
        qw.eq("code", code);
        return super.getOneObj(qw)
                .map(ModelFieldDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.ModelField, code));
    }

    private void checkShowInListCount(Integer modelId) {
        List<ModelFieldDto> fields = this.listByModelId(modelId);
        long count = fields.stream()
                .filter(ModelFieldDto::getShowInList)
                .count();
        if (count > 5) {
            throw new BusinessException(localeService.getMessage("showInListModelFiledCountLimit", 5));
        }
    }
}
