package com.eking.momp.model.manager.impl;

import com.eking.momp.db.model.Model;
import com.eking.momp.model.dto.*;
import com.eking.momp.model.manager.ModelManager;
import com.eking.momp.model.service.ModelFieldService;
import com.eking.momp.model.service.ModelRelationService;
import com.eking.momp.model.service.ModelService;
import com.eking.momp.model.service.RelationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelManagerImpl implements ModelManager {

    @Autowired
    private ModelRelationService modelRelationService;
    @Autowired
    private RelationTypeService relationTypeService;
    @Autowired
    private ModelFieldService modelFieldService;
    @Autowired
    private ModelService modelService;

    @Override
    public List<ModelDto> list(String keyword) {
        if (keyword == null) {
            return modelService.list();
        } else {
            return modelService.list().stream()
                    .filter(modelDto -> modelDto.getName().contains(keyword))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void clearLayerId(Integer layerId) {
        modelService.list().stream()
                .filter(model -> layerId.equals(model.getLayerId()))
                .forEach(model -> {
                    Model modelForUpdate = modelService.getForUpdate(model.getId());
                    modelForUpdate.setLayerId(null);
                    modelService.update(modelForUpdate);
                });
    }

    @Override
    public TopoDto<List<ModelTopoDto>, List<ModelRelationTopoDto>> getModelTopo(Integer rootModelId) {
        List<ModelTopoDto> models = new ArrayList<>();
        List<ModelRelationTopoDto> relations = new ArrayList<>();

        ModelTopoDto rootModel = genTopoModelDto(rootModelId.toString(), rootModelId, true, false, 0);
        models.add(rootModel);

        List<String> addedIds = new ArrayList<>();

        Pair<List<ModelTopoDto>, List<ModelRelationTopoDto>> pair1 = buildTopo(rootModel, 1, addedIds);
        transPairToResult(pair1, models, relations);

        pair1.getFirst().stream()
                .filter(m -> !m.getId().contains("_c"))
                .map(m -> buildTopo(m, 2, addedIds))
                .forEach(pair2 -> transPairToResult(pair2, models, relations));

        Pair<List<ModelTopoDto>, List<ModelRelationTopoDto>> pair1r = buildReverseTopo(rootModel, -1, addedIds);
        transPairToResult(pair1r, models, relations);

        pair1r.getFirst().stream()
                .filter(m -> !m.getId().contains("_c"))
                .map(m -> buildReverseTopo(m, -2, addedIds))
                .forEach(pair2r -> transPairToResult(pair2r, models, relations));

        return TopoDto.of(models, relations);
    }

    /**
     * id_level_r_c
     * <p>
     * root ID 不变 正向level1，ID_1 正向level2，ID_2 反向向level1，ID_1_r 反向向level2，ID_2_r
     * 副本末尾加_c
     */
    private Pair<List<ModelTopoDto>, List<ModelRelationTopoDto>> buildTopo(ModelTopoDto parentModel,
                                                                           Integer level, List<String> addedIds) {

        List<ModelTopoDto> models = new ArrayList<>();
        List<ModelRelationTopoDto> relations = new ArrayList<>();
        List<ModelRelationDto> mrs = modelRelationService.listByModelId(parentModel.getRealId());
        for (ModelRelationDto mr : mrs) {
            Integer realId = mr.getTargetModelId();

            String id = realId + "_" + level;
            int realLevel = level;
            if (parentModel.getRealId().equals(realId)) {
                id += "_c";
                realLevel -= 1;
            }
            ModelTopoDto model = genTopoModelDto(id, realId, false, realLevel == 0 || realLevel == 1, realLevel);// 0是root副本
            ModelRelationTopoDto relation = genTopoRelationDto(mr, parentModel, model);

            if (!addedIds.contains(model.getId())) {
                models.add(model);
                addedIds.add(model.getId());
            }
            relations.add(relation);
        }
        return Pair.of(models, relations);
    }

    private Pair<List<ModelTopoDto>, List<ModelRelationTopoDto>> buildReverseTopo(ModelTopoDto parentModel,
                                                                                  Integer level,
                                                                                  List<String> addedIds) {

        List<ModelTopoDto> models = new ArrayList<>();
        List<ModelRelationTopoDto> relations = new ArrayList<>();
        List<ModelRelationDto> mrs = modelRelationService.listByTargetModelId(parentModel.getRealId());
        for (ModelRelationDto mr : mrs) {
            Integer realId = mr.getModelId();

            String id = realId + "_" + level + "_r";
            int realLevel = level;
            if (parentModel.getRealId().equals(realId)) {
                realLevel += 1;
                if (realLevel == 0) {// root的副本在正向已经统计了，这里不统计，其他层的副本还要，正反像都展示副本
                    continue;
                }
                id += "_c";
            }
            ModelTopoDto model = genTopoModelDto(id, realId, false, realLevel == -1, realLevel);
            ModelRelationTopoDto relation = genTopoRelationDto(mr, model, parentModel);

            if (!addedIds.contains(model.getId())) {
                models.add(model);
                addedIds.add(model.getId());
            }
            relations.add(relation);
        }
        return Pair.of(models, relations);
    }

    private ModelTopoDto genTopoModelDto(String id, Integer realId, boolean root, boolean editable, int order) {
        ModelDto modelDto = modelService.getById(realId);
        ModelTopoDto topoModelDto = new ModelTopoDto(modelDto);
        topoModelDto.setId(id);
        topoModelDto.setRealId(realId);
        topoModelDto.setRoot(root);
        topoModelDto.setEditable(editable);
        topoModelDto.setShowOrder(order);
        return topoModelDto;
    }

    private ModelRelationTopoDto genTopoRelationDto(ModelRelationDto relation, ModelTopoDto model,
                                                    ModelTopoDto targetModel) {
        ModelRelationTopoDto topo = new ModelRelationTopoDto();
        topo.setId(relation.getId());
        topo.setCode(relation.getCode());
        topo.setName(relation.getName());
        topo.setRelationTypeId(relation.getRelationTypeId());
        topo.setMapping(relation.getMapping());
        topo.setModelId(model.getId());
        topo.setTargetModelId(targetModel.getId());
        topo.setModelFieldId(relation.getModelFieldId());
        topo.setDescription(relation.getDescription());
        RelationTypeDto relationType = relationTypeService.getById(relation.getRelationTypeId());
        topo.setRelationType(relationType);
        topo.setModel(model);
        topo.setTargetModel(targetModel);
        if (relation.getModelFieldId() != null) {
            ModelFieldDto modelField = modelFieldService.getById(relation.getModelFieldId());
            topo.setModelField(modelField);
        }
        return topo;
    }

    private void transPairToResult(Pair<List<ModelTopoDto>, List<ModelRelationTopoDto>> pair,
                                   List<ModelTopoDto> models, List<ModelRelationTopoDto> relations) {

        pair.getFirst().sort(Comparator.comparingInt(o -> Math.abs(o.getShowOrder())));
        models.addAll(pair.getFirst());
        relations.addAll(pair.getSecond());
    }
}
