package com.eking.momp.model.service.impl;

import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.AlreadyExistsException;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.mapper.ModelUniquenessMapper;
import com.eking.momp.db.model.ModelUniqueness;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.dto.ModelUniquenessDto;
import com.eking.momp.model.dto.ModelUniquenessItemDto;
import com.eking.momp.model.param.ModelUniquenessItemParam;
import com.eking.momp.model.param.ModelUniquenessParam;
import com.eking.momp.model.service.ModelUniquenessItemService;
import com.eking.momp.model.service.ModelUniquenessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
public class ModelUniquenessServiceImpl extends AbstractService<ModelUniquenessMapper, ModelUniqueness>
        implements ModelUniquenessService {

    @Autowired
    private ModelUniquenessItemService itemService;

    @Override
    public List<ModelUniquenessDto> listByModelId(Integer modelId) {
        List<ModelUniqueness> uniqs = super.listObjByProps(PropBean.of("model_id", modelId));
        return uniqs.stream()
                .map(ModelUniquenessDto::new)
                .peek(dto -> {
                    List<ModelUniquenessItemDto> items = itemService.listByModelUniquenessId(dto.getId());
                    dto.setItems(items);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ModelUniquenessDto getById(Integer id) {
        return super.getObjById(id)
                .map(ModelUniquenessDto::new)
                .map(uniq -> {
                    List<ModelUniquenessItemDto> items = itemService.listByModelUniquenessId(uniq.getId());
                    uniq.setItems(items);
                    return uniq;
                })
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Uniqueness, id));
    }

    @Override
    public ModelUniquenessDto save(ModelUniquenessParam param) {
        checkExist(null, param.getModelId(), param.getItems());

        ModelUniqueness entity = new ModelUniqueness();
        entity.setModelId(param.getModelId());
        entity.setRequired(param.getRequired());
        ModelUniqueness uniq = super.saveObj(entity);

        if (param.getItems() != null) {
            param.getItems().stream()
                    .peek(itemParam -> itemParam.setModelUniquenessId(uniq.getId()))
                    .forEach(itemService::save);
        }
        return new ModelUniquenessDto(uniq);
    }

    @Override
    public ModelUniquenessDto update(Integer id, ModelUniquenessParam param) {
        checkExist(id, param.getModelId(), param.getItems());

        ModelUniqueness uniq = super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Uniqueness, id));
        if (param.getRequired() != null) {
            uniq.setRequired(param.getRequired());
            super.updateObjById(uniq);
        }

        List<ModelUniquenessItemDto> preItems = itemService.listByModelUniquenessId(id);
        preItems.forEach(item -> itemService.delete(item.getId()));

        if (param.getItems() != null) {
            param.getItems().forEach(itemParam -> {
                itemParam.setModelUniquenessId(id);
                itemService.save(itemParam);
            });
        }
        return new ModelUniquenessDto(uniq);
    }

    @Override
    public void delete(Integer id) {
        List<ModelUniquenessItemDto> items = itemService.listByModelUniquenessId(id);
        items.forEach(item -> itemService.delete(item.getId()));
        super.deleteObj(id);
    }

    private void checkExist(Integer uniqId, Integer modelId, List<ModelUniquenessItemParam> thisItems) {
        Set<Integer> thisIds = thisItems.stream()
                .map(ModelUniquenessItemParam::getModelFieldId)
                .collect(Collectors.toSet());
        List<ModelUniqueness> uniqs = super.listObjByProps(PropBean.of("model_id", modelId));
        uniqs.stream()
                .filter(uniq -> uniqId == null || !uniqId.equals(uniq.getId()))
                .map(uniq -> itemService.listByModelUniquenessId(uniq.getId()))
                .map(items -> items.stream()
                        .map(ModelUniquenessItemDto::getModelFieldId)
                        .collect(Collectors.toSet()))
                .filter(ids -> compareSet(ids, thisIds))
                .findAny()
                .ifPresent(ids -> {
                    throw new AlreadyExistsException(localeService.getLabel("uniqueness"));
                });
    }

    private <T> boolean compareSet(Set<T> aList, Set<T> bList) {
        if (aList.size() != bList.size()) {
            return false;
        }
        boolean notSame = aList.stream()
                .anyMatch(a -> !bList.contains(a));
        return !notSame;
    }
}
