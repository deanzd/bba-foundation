package com.eking.momp.model.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.common.Constant;
import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.InUsedException;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.AbstractService;
import com.eking.momp.db.mapper.ModelMapper;
import com.eking.momp.db.model.Model;
import com.eking.momp.model.dto.LayerDto;
import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.param.ModelParam;
import com.eking.momp.model.service.*;
import com.eking.momp.model.stream.EntitySearchSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@CacheConfig(cacheNames = "model")
@Service
@Transactional
public class ModelServiceImpl extends AbstractService<ModelMapper, Model> implements ModelService {
    @Autowired
    private LayerService layerService;
    @Autowired
    private ModelFieldService modelFieldService;
    @Autowired
    private EntityService entityService;
    @Autowired
    private ModelUniquenessService modelUniquenessService;
    @Autowired
    private EntitySearchSender entitySearchSender;

    //    @Cacheable(key = "'all'")
    @Override
    public List<ModelDto> list() {
        return super.listObjs().stream()
                .map(ModelDto::new)
                .peek(modelDto -> {
                    if (modelDto.getLayerId() != null) {
                        LayerDto layerDto = layerService.getById(modelDto.getLayerId());
                        modelDto.setLayerDto(layerDto);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public ModelDto save(ModelParam param) {
        checkObjExisted("code", PropBean.of("code", param.getCode()));
        checkObjExisted("name", PropBean.of("name", param.getName()));
        if (Boolean.TRUE.equals(param.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        Model model = new Model();
        model.setCode(param.getCode());
        model.setName(param.getName());
        model.setDescription(param.getDescription());
        model.setIconImage(param.getIconImage());
        model.setLayerId(param.getLayerId());
        model.setShowOrder(param.getShowOrder());
        model.setSysInit(param.getSysInit());
        Model returnModel = super.saveObj(model);
        return new ModelDto(returnModel);
    }

    //    @Caching(evict = {
//            @CacheEvict(key = "#id"),
//            @CacheEvict(key = "'all'"),
//            @CacheEvict(key = "#result.code")
//    })
    @Override
    public ModelDto update(Integer id, ModelParam param) {
        checkObjExisted(id, "code", PropBean.of("code", param.getCode()));
        checkObjExisted(id, "name", PropBean.of("name", param.getName()));

        Model model = super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Model, id));

        if (Boolean.TRUE.equals(param.getSysInit()) || Boolean.TRUE.equals(model.isSysInit())) {
            checkRoleAcl("sysadmin");
        }

        if (param.getCode() != null) {
            model.setCode(param.getCode());
        }
        boolean updateSearch = false;
        if (param.getName() != null && model.getName().equals(param.getName())) {
            model.setName(param.getName());
            updateSearch = true;
        }
        if (param.getIconImage() != null) {
            model.setIconImage(param.getIconImage());
        }
        if (param.getLayerId() != null && model.getLayerId() != param.getLayerId()) {
            model.setLayerId(param.getLayerId());
            updateSearch = true;
        }
        if (param.getSysInit() != null) {
            model.setSysInit(param.getSysInit());
        }
        if (param.getShowOrder() != null) {
            model.setShowOrder(param.getShowOrder());
        }
        model.setDescription(param.getDescription());
        Model returnModel = super.updateObjById(model);

        if (updateSearch) {
            entitySearchSender.updateModel(model.getCode());
        }

        return new ModelDto(returnModel);
    }

    //    @Caching(evict = {
//            @CacheEvict(key = "#model.id"),
//            @CacheEvict(key = "#result.code"),
//            @CacheEvict(key = "'all'")
//    })
    @Override
    public ModelDto update(Model model) {
        if (Boolean.TRUE.equals(model.isSysInit())) {
            checkRoleAcl("sysadmin");
        }

        super.getObjById(model.getId()).orElseThrow(() -> new ResourceNotFoundException(ResourceType.Model,
                model.getId()));
        if (model.getCode() != null) {
            checkObjExisted(model.getId(), "code", PropBean.of("code", model.getCode()));
        }
        if (model.getName() != null) {
            checkObjExisted(model.getId(), "name", PropBean.of("name", model.getName()));
        }

        Model returnModel = super.updateObjById(model);
        return new ModelDto(returnModel);
    }

    //    @Caching(evict = {
//            @CacheEvict(key = "#result.id"),
//            @CacheEvict(key = "#result.code"),
//            @CacheEvict(key = "'all'")
//    })
    @Override
    public Model delete(Integer id) {
        Model model = super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Model, id));

        if (Boolean.TRUE.equals(model.isSysInit())) {
            checkRoleAcl("sysadmin");
        }

        // 正在被实例使用
        entityService.listAll(model.getCode())
                .stream()
                .findFirst()
                .ifPresent(entity -> {
                    throw new InUsedException(ResourceType.Model, model.getName(),
                            ResourceType.Entity, entity.get("id").toString());
                });
        // 正在被唯一性约束使用
        modelUniquenessService.listByModelId(id)
                .stream()
                .findAny()
                .ifPresent(uniq -> {
                    throw new InUsedException(ResourceType.Model, model.getName(), ResourceType.Uniqueness,
                            uniq.getId());
                });
        // 删除
        modelFieldService.listByModelId(id)
                .forEach(field -> modelFieldService.delete(field.getId()));
        super.deleteObj(id);
        return model;
    }

    //    @Cacheable(key = "#id")
    @Override
    public ModelDto getById(Integer id) {
        return super.getObjById(id)
                .map(ModelDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Model, id));
    }

    @Override
    public Model getForUpdate(Integer id) {
        return super.getObjById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Model, id));
    }

    //    @Cacheable(key = "#code")
    @Override
    public ModelDto getByCode(String code) {
        QueryWrapper<Model> qw = new QueryWrapper<>();
        qw.eq("code", code);
        return super.getOneObj(qw)
                .map(ModelDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Model, code));
    }

    @Override
    public List<ModelDto> listByLayerId(Integer layerId) {
        return super.listObjByProps(Constant.FIELD_SHOW_ORDER, true, PropBean.of("layer_id", layerId))
                .stream().map(ModelDto::new)
                .collect(Collectors.toList());
    }

}
