package com.eking.momp.model.service.impl;

import com.eking.momp.common.bean.PropBean;
import com.eking.momp.db.mapper.ModelUniquenessItemMapper;
import com.eking.momp.db.model.ModelUniquenessItem;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.dto.ModelFieldDto;
import com.eking.momp.model.dto.ModelUniquenessItemDto;
import com.eking.momp.model.param.ModelUniquenessItemParam;
import com.eking.momp.model.service.ModelFieldService;
import com.eking.momp.model.service.ModelUniquenessItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ModelUniquenessItemServiceImpl extends AbstractService<ModelUniquenessItemMapper, ModelUniquenessItem>
        implements ModelUniquenessItemService {

    @Autowired
    private ModelFieldService modelFieldService;

    @Override
    public List<ModelUniquenessItemDto> listByModelUniquenessId(Integer modelUniquenessId) {
        return super.listObjByProps(PropBean.of("model_uniqueness_id", modelUniquenessId)).stream()
                .map(ModelUniquenessItemDto::new)
                .peek(dto -> {
                    ModelFieldDto field = modelFieldService.getById(dto.getModelFieldId());
                    dto.setModelFieldName(field.getName());
                    dto.setModelFieldCode(field.getCode());
                })
                .collect(Collectors.toList());
    }

    @Override
    public ModelUniquenessItemDto save(ModelUniquenessItemParam param) {
        ModelUniquenessItem item = new ModelUniquenessItem(param.getModelUniquenessId(), param.getModelFieldId());
        ModelUniquenessItem returnItem = super.saveObj(item);
        return new ModelUniquenessItemDto(returnItem);
    }

    @Override
    public void delete(Integer id) {
        super.deleteObj(id);
    }

    @Override
    public List<ModelUniquenessItemDto> listByModelFieldId(Integer modelFieldId) {
        return super.listObjByProps(PropBean.of("model_field_id", modelFieldId))
                .stream()
                .map(ModelUniquenessItemDto::new)
                .collect(Collectors.toList());
    }

}
