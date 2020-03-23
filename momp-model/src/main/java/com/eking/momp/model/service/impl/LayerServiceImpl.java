package com.eking.momp.model.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.common.Constant;
import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.db.mapper.LayerMapper;
import com.eking.momp.db.model.Layer;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.dto.LayerDto;
import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.manager.ModelManager;
import com.eking.momp.model.param.LayerParam;
import com.eking.momp.model.service.LayerService;
import com.eking.momp.model.service.ModelService;
import com.eking.momp.model.stream.EntitySearchSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LayerServiceImpl extends AbstractService<LayerMapper, Layer> implements LayerService {
    @Autowired
    private ModelService modelService;
    @Autowired
    private ModelManager modelManager;
    @Autowired
    private EntitySearchSender searchSender;

    @Override
    public List<LayerDto> listWithModels(String keyword) {
        List<ModelDto> models = modelService.list();
        if (keyword == null) {
            return this.list().stream()
                    .peek(layer -> {
                        List<ModelDto> fitModels = models.stream()
                                .filter(model -> layer.getId().equals(model.getLayerId()))
                                .collect(Collectors.toList());
                        layer.setModels(fitModels);
                    })
                    .collect(Collectors.toList());
        } else {
            return this.list().stream()
                    .peek(layer -> {
                        List<ModelDto> fitModels = models.stream()
                                .filter(model -> layer.getId().equals(model.getLayerId()))
                                .filter(model -> model.getName().contains(keyword))
                                .collect(Collectors.toList());
                        layer.setModels(fitModels);
                    })
                    .filter(layerDto -> layerDto.getModels().size() > 0)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<LayerDto> list() {
        List<LayerDto> result = new ArrayList<>();
        QueryWrapper<Layer> qw = new QueryWrapper<>();
        List<Layer> layers = super.listObjs(Constant.FIELD_SHOW_ORDER, true, qw);
        layers.forEach(layer -> result.add(new LayerDto(layer)));
        return result;
    }

    @Override
    public LayerDto getById(Integer id) {
        return super.getObjById(id).map(LayerDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Layer, id));
    }

    @Override
    public LayerDto save(LayerParam param) {
        checkObjExisted("code", PropBean.of("code", param.getCode()));
        checkObjExisted("name", PropBean.of("name", param.getName()));
        if (Boolean.TRUE.equals(param.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        Layer layer = new Layer();
        layer.setCode(param.getCode());
        layer.setName(param.getName());
        layer.setDescription(param.getDescription());
        layer.setIconImage(param.getIconImage());
        layer.setShowOrder(param.getShowOrder());
        if (param.getSysInit() != null) {
            layer.setSysInit(param.getSysInit());
        }
        Layer returnLayer = saveObj(layer);
        return new LayerDto(returnLayer);
    }

    @Override
    public LayerDto update(Integer layerId, LayerParam param) {
        checkObjExisted(layerId, "code", PropBean.of("code", param.getCode()));
        checkObjExisted(layerId, "name", PropBean.of("name", param.getName()));

        Layer layer = super.getObjById(layerId)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Layer, layerId));

        if (Boolean.TRUE.equals(layer.isSysInit()) || Boolean.TRUE.equals(param.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        if (param.getCode() != null) {
            layer.setCode(param.getCode());
        }
        boolean updateName = false;
        if (param.getName() != null && !layer.getName().equals(param.getName())) {
            layer.setName(param.getName());
            updateName = true;
        }
        if (param.getSysInit() != null) {
            layer.setSysInit(param.getSysInit());
        }
        layer.setDescription(param.getDescription());
        layer.setIconImage(param.getIconImage());
        layer.setShowOrder(param.getShowOrder());
        Layer returnLayer = updateObjById(layer);

        if (updateName) {
            modelService.listByLayerId(layerId).stream()
                    .map(ModelDto::getCode)
                    .forEach(searchSender::updateModel);
        }

        return new LayerDto(returnLayer);
    }

    @Override
    public void delete(Integer id) {
        LayerDto layer = this.getById(id);
        if (Boolean.TRUE.equals(layer.getSysInit())) {
            checkRoleAcl("sysadmin");
        }

        modelManager.clearLayerId(id);
        super.deleteObj(id);
    }

}
