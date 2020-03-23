package com.eking.momp.model.service.impl;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.common.Constant;
import com.eking.momp.common.DataType;
import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PageBean;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.AlreadyExistsException;
import com.eking.momp.common.exception.BusinessException;
import com.eking.momp.common.exception.InUsedException;
import com.eking.momp.common.exception.ResourceNotFoundException;
import com.eking.momp.common.param.PageParam;
import com.eking.momp.common.service.LocaleService;
import com.eking.momp.db.model.DimensionModel;
import com.eking.momp.db.model.ModelField;
import com.eking.momp.model.dto.*;
import com.eking.momp.model.service.*;
import com.eking.momp.model.stream.EntitySearchSender;
import com.eking.momp.mongo.AbstractEntityService;
import com.eking.momp.mongo.model.EntityRelation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
//@Transactional("mongoTransactionManager")
public class EntityServiceImpl extends AbstractEntityService implements EntityService {
    @Autowired
    private ModelUniquenessService modelUniquenessService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private ModelFieldService modelFieldService;
    @Autowired
    private EntityRelationService entityRelationService;
    @Autowired
    private ModelRelationService modelRelationService;
    @Autowired
    private RelationTypeService relationTypeService;
    @Autowired
    private DimensionService dimensionService;
    @Autowired
    private DimensionModelService dimensionModelService;
    @Autowired
    private DimensionRelationService dimensionRelationService;
    @Autowired
    private LocaleService localeService;
    @Autowired
    private LayerService layerService;
    @Autowired
    private EntitySearchSender entitySearchSender;
    @Autowired
    private EntitySearchService entitySearchService;

    @Override
    public ModelEntitiesDto getModelEntities(Integer dimensionId, String keyword) {
        DimensionDto dimension = dimensionService.getById(dimensionId);
        ModelDto modelDto = modelService.getById(dimension.getModelId());
        ModelEntitiesDto dto = new ModelEntitiesDto(modelDto);

        QueryWrapper<ModelField> qw = new QueryWrapper<>();
        qw.eq("model_id", modelDto.getId());
        List<ModelFieldDto> fieldDtos = modelFieldService.list(qw);
        dto.setFields(fieldDtos);

        // 模糊查询
        List<Map<String, Object>> entities;
        if (keyword != null && keyword.length() > 0) {
            List<Criteria> criterias = new ArrayList<>();
            criterias.add(Criteria.where(Constant.FIELD_ID_MONGO).is(keyword));
            fieldDtos.forEach(field -> {
                if (Boolean.TRUE.equals(field.getShowInTable())) {
                    criterias.add(Criteria.where(field.getCode()).regex(keyword));
                }
            });
            Query query = Query.query(new Criteria().orOperator(criterias.toArray(new Criteria[]{})));
            entities = this.list(modelDto.getCode(), query);
        } else {
            entities = this.listAll(modelDto.getCode());
        }
        dto.setEntities(entities);
        return dto;
    }

    @Override
    public TopoDto<List<ModelEntitiesDto>, List<EntityRelationDto>> getTopo(String modelCode, String entityId,
                                                                            Integer dimensionId) {
        List<Integer> allowModelRelationIds = dimensionRelationService.listModelRelationIdsByDimensionId(dimensionId);
        if (allowModelRelationIds == null || allowModelRelationIds.size() == 0) {
            return null;
        }

        Map<Integer, ModelEntitiesDto> modelEntitiesMap = new HashMap<>();
        Map<String, EntityRelationDto> relationMap = new HashMap<>();
        buildEntitiesRecursion(modelCode, entityId, modelEntitiesMap, new ArrayList<>(), relationMap,
                allowModelRelationIds);

        List<ModelEntitiesDto> modelEntities = new ArrayList<>();
        convertMap2List(dimensionId, modelEntitiesMap, modelEntities);

        List<EntityRelationDto> relations = new ArrayList<>();
        relationMap.forEach((key, value) -> relations.add(value));
        return TopoDto.of(modelEntities, relations);
    }

    private void buildEntitiesRecursion(String modelCode, String entityId,
                                        Map<Integer, ModelEntitiesDto> modelEntitiesMap, List<String> addedEntities,
                                        Map<String, EntityRelationDto> relationMap,
                                        List<Integer> allowModelRelationIds) {
        if (addedEntities.contains(entityId)) {
            return;
        }
        Map<String, Object> entity = this.getById(modelCode, entityId);
        ModelDto model = modelService.getByCode(modelCode);
        ModelEntitiesDto dto = modelEntitiesMap.get(model.getId());
        if (dto == null) {
            dto = new ModelEntitiesDto(model);
            QueryWrapper<ModelField> fieldQw = new QueryWrapper<>();
            fieldQw.eq("model_id", model.getId());
            List<ModelFieldDto> fieldDtos = modelFieldService.list(fieldQw);
            dto.setFields(fieldDtos);
            dto.setEntities(new ArrayList<>());
            modelEntitiesMap.put(model.getId(), dto);
        }
        List<Map<String, Object>> entities = dto.getEntities();
        entities.add(entity);
        addedEntities.add(entityId);
        // 正向
        List<EntityRelation> relations = entityRelationService.listByEntityIdAndModelRelationIds(entityId,
                allowModelRelationIds);
        for (EntityRelation relation : relations) {
            if (relationMap.get(relation.getId()) == null) {
                putRelationMap(relationMap, relation);
                buildEntitiesRecursion(relation.getTargetEntityModel(), relation.getTargetEntityId(), modelEntitiesMap,
                        addedEntities, relationMap, allowModelRelationIds);
            }
        }
        // 反向
        List<EntityRelation> reverseRelations = entityRelationService.listByTargetEntityIdAndModelRelationIds(entityId,
                allowModelRelationIds);
        for (EntityRelation relation : reverseRelations) {
            if (relationMap.get(relation.getId()) == null) {
                putRelationMap(relationMap, relation);
                buildEntitiesRecursion(relation.getEntityModel(), relation.getEntityId(), modelEntitiesMap,
                        addedEntities, relationMap, allowModelRelationIds);
            }
        }
    }

    private void putRelationMap(Map<String, EntityRelationDto> relationMap, EntityRelation relation) {
        EntityRelationDto relationDto = new EntityRelationDto(relation);
        ModelRelationDto modelRelationDto = modelRelationService.getById(relation.getModelRelationId());
        RelationTypeDto relationTypeDto = relationTypeService.getById(modelRelationDto.getRelationTypeId());
        relationDto.setRelationType(relationTypeDto);
        relationMap.put(relation.getId(), relationDto);
    }

    private void convertMap2List(Integer dimensionId, Map<Integer, ModelEntitiesDto> modelEntitiesMap,
                                 List<ModelEntitiesDto> result) {
        // 设置model的order，DimensionModel现在就这一个作用
        QueryWrapper<DimensionModel> qw = new QueryWrapper<>();
        qw.eq("dimension_id", dimensionId);
        List<DimensionModel> dimensionModels = dimensionModelService.list(qw);
        Map<Integer, Integer> modelOrderMap = new HashMap<>();
        dimensionModels.forEach(
                dimensionModel -> modelOrderMap.put(dimensionModel.getModelId(), dimensionModel.getShowOrder()));

        modelEntitiesMap.forEach((key, value) -> {
            result.add(value);
            Integer order = modelOrderMap.get(key);
            value.setShowOrder(order != null ? order : 0);
        });
        result.sort(Comparator.comparing(ModelEntitiesDto::getShowOrder));
    }


    @Override
    public TopoDto<List<ModelEntitiesDto>, List<EntityRelationTopoDto>> getTopoLevel2(String rootModelCode,
                                                                                      String rootEntityId,
                                                                                      List<Integer> modelRelationIds,
                                                                                      List<Integer> modelRelationIdsR) {
        List<Map<String, Object>> entities = new ArrayList<>();
        List<EntityRelationTopoDto> relations = new ArrayList<>();

        Map<String, Object> rootEntity = genEntityTopoDto(rootModelCode, rootEntityId, rootEntityId, true, false, 0);
        entities.add(rootEntity);

        List<String> addedIds = new ArrayList<>();

        Pair<List<Map<String, Object>>, List<EntityRelationTopoDto>> pair1 = buildTopo(rootEntity, 1, addedIds,
                modelRelationIds);
        transPairToResult(pair1, entities, relations);

        pair1.getFirst().stream()
                .filter(m -> !m.get("id").toString().contains("_c"))
                .map(m -> buildTopo(m, 2, addedIds, null))
                .forEach(pair2 -> transPairToResult(pair2, entities, relations));

        Pair<List<Map<String, Object>>, List<EntityRelationTopoDto>> pair1r = buildTopoReverse(rootEntity, -1,
                addedIds, modelRelationIdsR);
        transPairToResult(pair1r, entities, relations);

        pair1r.getFirst().stream()
                .filter(m -> !m.get("id").toString().contains("_c"))
                .map(m -> buildTopoReverse(m, -2, addedIds, null))
                .forEach(pair2r -> transPairToResult(pair2r, entities, relations));

        Map<String, List<Map<String, Object>>> modelEntityMap = new HashMap<>();
        for (Map<String, Object> entity : entities) {
            String modelCode = entity.get("temp_model_code_101").toString();
            List<Map<String, Object>> list = modelEntityMap.computeIfAbsent(modelCode, k -> new ArrayList<>());
            list.add(entity);
        }
        List<ModelEntitiesDto> modelEntitiesDtos = new ArrayList<>();
        modelEntityMap.forEach((m, list) -> {
            ModelDto modelDto = modelService.getByCode(m);
            ModelEntitiesDto modelEntitiesDto = new ModelEntitiesDto(modelDto);
            QueryWrapper<ModelField> fieldQw = new QueryWrapper<>();
            fieldQw.eq("model_id", modelDto.getId());
            List<ModelFieldDto> fieldDtos = modelFieldService.list(fieldQw);
            modelEntitiesDto.setFields(fieldDtos);
            modelEntitiesDto.setEntities(list);
            modelEntitiesDtos.add(modelEntitiesDto);
        });

        return TopoDto.of(modelEntitiesDtos, relations);
    }

    @Override
    public void export(String modelCode, String keyword, HttpServletResponse response) {
        ModelDto model = modelService.getByCode(modelCode);
        ServletOutputStream out;
        try {
            out = response.getOutputStream();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(localeService.getMessage("exportEntitiesFaild"));
        }
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
        Sheet sheet = new Sheet(1, 0);

        List<ModelFieldDto> fields = modelFieldService.listByModelId(model.getId());
        List<Map<String, Object>> entities;
        if (keyword != null && keyword.length() > 0) {
            List<Criteria> criterias = new ArrayList<>();
            criterias.add(Criteria.where(Constant.FIELD_ID_MONGO).is(keyword));
            fields.forEach(field -> {
                if (Boolean.TRUE.equals(field.getShowInTable())) {
                    criterias.add(Criteria.where(field.getCode()).regex(keyword));
                }
            });
            Query query = Query.query(new Criteria().orOperator(criterias.toArray(new Criteria[]{})));
            entities = this.list(model.getCode(), query);
        } else {
            entities = this.listAll(model.getCode());
        }
        //第一个sheet，entities本身
        buildSheet(writer, sheet, fields, entities, false, model.getCode() + "(" + model.getName() + ")");
        //正向关系的实例
        List<ModelRelationDto> r0List = modelRelationService.listByModelId(model.getId());
        Map<Integer, List<Map<String, Object>>> map0 = r0List.stream()
                .collect(Collectors.toMap(ModelRelationDto::getId, mr -> new ArrayList<>()));
        entities.stream()
                .flatMap(e -> entityRelationService.listByEntityId(e.get("id").toString()).stream())
                .forEach(er -> {
                    if (map0.containsKey(er.getModelRelationId())) {
                        List<Map<String, Object>> es = map0.get(er.getModelRelationId());
                        Map<String, Object> e = this.getById(er.getTargetEntityModel(),
                                er.getTargetEntityId());
                        e.put("ref_entity_id", er.getEntityId());
                        es.add(e);
                    }
                });
        int sheetNum = 2;
        for (Map.Entry<Integer, List<Map<String, Object>>> entry : map0.entrySet()) {
            Sheet sheetN = new Sheet(sheetNum, 0);
            Integer mrId = entry.getKey();
            List<Map<String, Object>> targetEntities = entry.getValue();
            ModelRelationDetailDto mr = modelRelationService.getDetailById(mrId);
            List<ModelFieldDto> targetFields = modelFieldService.listByModelId(mr.getTargetModelId());
            buildSheet(writer, sheetN, targetFields, targetEntities, true,
                    mr.getCode() + "(" + mr.getModel().getName() + " -> " + mr.getTargetModel().getName() + ")");
            sheetNum++;
        }
        //反向关系的实例
        List<ModelRelationDto> r1List = modelRelationService.listByTargetModelId(model.getId());
        Map<Integer, List<Map<String, Object>>> map1 = r1List.stream()
                .filter(r -> !r.getModelId().equals(r.getTargetModelId()))
                .collect(Collectors.toMap(ModelRelationDto::getId, mr -> new ArrayList<>()));
        entities.stream()
                .flatMap(e -> entityRelationService.listByTargetEntityId(e.get("id").toString()).stream())
                .forEach(er -> {
                    if (map1.containsKey(er.getModelRelationId())) {
                        List<Map<String, Object>> es = map1.get(er.getModelRelationId());
                        if (es != null) {
                            Map<String, Object> e = this.getById(er.getEntityModel(), er.getEntityId());
                            e.put("ref_entity_id", er.getTargetEntityId());
                            es.add(e);
                        }
                    }
                });
        for (Map.Entry<Integer, List<Map<String, Object>>> entry : map1.entrySet()) {
            Sheet sheetN = new Sheet(sheetNum, 0);
            Integer mrId = entry.getKey();
            List<Map<String, Object>> entities1 = entry.getValue();
            ModelRelationDetailDto mr = modelRelationService.getDetailById(mrId);
            List<ModelFieldDto> fields1 = modelFieldService.listByModelId(mr.getModelId());
            buildSheet(writer, sheetN, fields1, entities1, true, mr.getCode() + "(" + mr.getModel().getName() + " -> "
                    + mr.getTargetModel().getName() + ")");
            sheetNum++;
        }

        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        String nowStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("_yyyy-MM-dd HH-mm"));
        response.setHeader("Content-disposition", "attachment;filename=" + model.getCode() + nowStr + ".xlsx");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
        try {
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.finish();
    }

    @Override
    public PageBean<EntityDto> search(String keyword, String lastId) {
        if (lastId == null && keyword.length() > 12) {//按ID查询
            for (ModelDto model : modelService.list()) {
                if (super.getEntityById(model.getCode(), keyword).isPresent()) {
                    EntityDto entity = getEntityDto(model.getCode(), keyword);
                    return PageBean.of(Collections.singletonList(entity), 1, 1);
                }
            }
        }
        PageBean<EntityIdDto> page = entitySearchService.search(keyword, lastId);
        List<EntityDto> entities = page.getRows().stream()
                .map(entityIdDto -> {
                    String modelCode = entityIdDto.getModelCode();
                    String id = entityIdDto.getId();
                    return getEntityDto(modelCode, id);
                }).collect(Collectors.toList());
        return PageBean.of(entities, page.getTotal(), page.getPages());
    }

    private EntityDto getEntityDto(String modelCode, String id) {
        EntityDto entityDto = new EntityDto();
        ModelDto model = modelService.getByCode(modelCode);
        LayerDto layer = layerService.getById(model.getLayerId());
        model.setLayerDto(layer);
        Map<String, Object> entity = this.getById(modelCode, id);
        List<ModelFiledValueDto> fields = modelFieldService.listByModelId(model.getId()).stream()
                .map(field -> {
                    ModelFiledValueDto filedValue = new ModelFiledValueDto();
                    filedValue.setCode(field.getCode());
                    filedValue.setName(field.getName());
                    filedValue.setDataType(field.getDataType());
                    filedValue.setShowInList(field.getShowInList());
                    filedValue.setValue(entity.get(field.getCode()));
                    return filedValue;
                }).collect(Collectors.toList());

        entityDto.setId(id);
        entityDto.setModel(model);
        entityDto.setFields(fields);
        return entityDto;
    }

    private void buildSheet(ExcelWriter writer, Sheet sheet, List<ModelFieldDto> fields,
                            List<Map<String, Object>> entities, boolean ref, String sheetName) {
        sheet.setSheetName(excelSheetNameReplace(sheetName));
        sheet.setAutoWidth(true);
        Map<String, DataType> headCodeMap = new LinkedHashMap<>();
        List<List<String>> head = new ArrayList<>();
        //列名称排序
        Collections.sort(fields, new Comparator<ModelFieldDto>() {
			@Override
			public int compare(ModelFieldDto o1, ModelFieldDto o2) {
				int sort1 = (o1.getShowOrder() == null ? 0 : o1.getShowOrder());
				int sort2 = (o2.getShowOrder() == null ? 0 : o2.getShowOrder());
				int diff = sort1 - sort2 ;
				if(diff > 0 ) {
					return 1;
				}else if(diff < 0) {
					return -1;
				}
				return 0;
			}}
        );
        //处理相邻列名称重复，导致合并单元格显示。
        for(int i = 0 ; i < fields.size()-1; i++ ){
		    if(fields.get(i).getName().equals(fields.get(i+1).getName())){
		    	fields.get(i+1).setName(fields.get(i+1).getName() + " ");
		    }
        }
        fields.forEach(field -> {
            List<String> h = new ArrayList<>();
            String headName = field.getName();
            if (headName.equals(field.getCode())) {
                headName = headName + " ";
            }
            h.add(headName);
            h.add(field.getCode());
            head.add(h);
            headCodeMap.put(field.getCode(), DataType.of(field.getDataType()));
        });
        List<String> h = new ArrayList<>();
        h.add("实例ID");
        h.add("id");
        head.add(h);
        headCodeMap.put("id", DataType.VARCHAR);
        if (ref) {
            List<String> h1 = new ArrayList<>();
            h1.add("关联实例ID");
            h1.add("ref_entity_id");
            head.add(h1);
            headCodeMap.put("ref_entity_id", DataType.VARCHAR);
        }
        sheet.setHead(head);
        List<List<String>> data = entities.stream()
                .map(entity -> {
                    List<String> valList = new ArrayList<>();
                    headCodeMap.forEach((code, dataType) -> {
                        Object val = entity.get(code);
                        if (val == null) {
                            valList.add("");
                        } else {
                            switch (dataType) {
                                case DATE:
                                    try {
                                        Date date = (Date) val;
                                        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();
                                        String str = localDateTime
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                        valList.add(str);
                                    } catch (Exception e) {
                                        valList.add(val.toString());
                                    }
                                    break;
                                case DATETIME:
                                    try {
                                        Date date = (Date) val;
                                        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();
                                        String str = localDateTime
                                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                        valList.add(str);
                                    } catch (Exception e) {
                                        valList.add(val.toString());
                                    }
                                    break;
                                case BOOLEAN:
                                    Boolean boo = (Boolean) val;
                                    valList.add(boo ? "是" : "否");
                                    break;
                                case DOUBLE:
                                    Double dou = Double.valueOf(val.toString());
                                    valList.add(String.format("%.2f", dou));
                                    break;
                                default:
                                    valList.add(val.toString());
                            }
                        }
                    });
                    return valList;
                })
                .collect(Collectors.toList());
        writer.write0(data, sheet);
    }

    private String excelSheetNameReplace(String sheetName) {
    	if(sheetName != null && !sheetName.isEmpty()) {
    		sheetName = sheetName.replace("/", "");
    		sheetName = sheetName.replace("\\", "");
    		sheetName = sheetName.replace("*", "");
    		sheetName = sheetName.replace("?", "");
    		sheetName = sheetName.replace("[", "");
    		sheetName = sheetName.replace("]", "");
    	}else {
    		sheetName = " " ;
    	}
    	return sheetName;
    }

    /**
     * id_level_r_c
     * <p>
     * root ID 不变 正向level1，ID_1 正向level2，ID_2 反向向level1，ID_1_r 反向向level2，ID_2_r
     * 副本末尾加_c
     */
    private Pair<List<Map<String, Object>>, List<EntityRelationTopoDto>> buildTopo(Map<String, Object> parentEntity,
                                                                                   Integer level,
                                                                                   List<String> addedIds,
                                                                                   List<Integer> allowModelRelationIds) {
        List<Map<String, Object>> entities = new ArrayList<>();
        List<EntityRelationTopoDto> relations = new ArrayList<>();
        entityRelationService.listByEntityId(parentEntity.get("realId").toString()).stream()
                .filter(er -> allowModelRelationIds == null || allowModelRelationIds.size() == 0 || allowModelRelationIds.contains(er.getModelRelationId()))
                .forEach(er -> {
                    String realId = er.getTargetEntityId();
                    String modelCode = er.getTargetEntityModel();

                    String id = realId + "_" + level;
                    int realLevel = level;
                    if (parentEntity.get("realId").equals(realId)) {
                        id += "_c";
                        realLevel -= 1;
                    }
                    Map<String, Object> entity = genEntityTopoDto(modelCode, id, realId, false,
                            realLevel == 0 || realLevel == 1,
                            realLevel);// 0是root副本
                    EntityRelationTopoDto relation = genEntityRelationTopoDto(er, parentEntity.get("id").toString(),
                            id);

                    if (!addedIds.contains(id)) {
                        entities.add(entity);
                        addedIds.add(id);
                    }
                    relations.add(relation);
                });
        return Pair.of(entities, relations);
    }

    private Pair<List<Map<String, Object>>, List<EntityRelationTopoDto>> buildTopoReverse(Map<String, Object> parentEntity,
                                                                                          Integer level,
                                                                                          List<String> addedIds,
                                                                                          List<Integer> allowModelRelationIds) {
        List<Map<String, Object>> entities = new ArrayList<>();
        List<EntityRelationTopoDto> relations = new ArrayList<>();
        entityRelationService.listByTargetEntityId(parentEntity.get("realId").toString()).stream()
                .filter(er -> allowModelRelationIds == null || allowModelRelationIds.size() == 0 || allowModelRelationIds.contains(er.getModelRelationId()))
                .forEach(er -> {
                    String realId = er.getEntityId();
                    String modelCode = er.getEntityModel();

                    String id = realId + "_" + level;
                    int realLevel = level;
                    if (parentEntity.get("realId").equals(realId)) {
                        id += "_c";
                        realLevel -= 1;
                    }
                    Map<String, Object> entity = genEntityTopoDto(modelCode, id, realId, false,
                            realLevel == 0 || realLevel == 1,
                            realLevel);// 0是root副本
                    EntityRelationTopoDto relation = genEntityRelationTopoDto(er, id,
                            parentEntity.get("id").toString());

                    if (!addedIds.contains(id)) {
                        entities.add(entity);
                        addedIds.add(id);
                    }
                    relations.add(relation);
                });
        return Pair.of(entities, relations);
    }

    private Map<String, Object> genEntityTopoDto(String modelCode, String id, String realId, boolean root,
                                                 boolean editable,
                                                 int order) {
        Map<String, Object> entity = this.getById(modelCode, realId);
        Map<String, Object> entityDto = new LinkedHashMap<>();
        entity.forEach(entityDto::put);
        entityDto.put("id", id);
        entityDto.put("realId", realId);
        entityDto.put("root", root);
        entityDto.put("editable", editable);
        entityDto.put("showOrder", order);
        entityDto.put("temp_model_code_101", modelCode);
        return entityDto;
    }

    private EntityRelationTopoDto genEntityRelationTopoDto(EntityRelationDto dto, String entityId,
                                                           String targetEntityId) {

        EntityRelationTopoDto topoDto = new EntityRelationTopoDto();
        topoDto.setId(dto.getId());
        topoDto.setEntityId(entityId);
        topoDto.setTargetEntityId(targetEntityId);
        ModelRelationDetailDto modelrelation = modelRelationService.getDetailById(dto.getModelRelationId());
        topoDto.setModelRelation(modelrelation);
        return topoDto;
    }

    private void transPairToResult(Pair<List<Map<String, Object>>, List<EntityRelationTopoDto>> pair,
                                   List<Map<String, Object>> models, List<EntityRelationTopoDto> relations) {

        pair.getFirst().sort(Comparator.comparingInt(o -> Math.abs(Integer.parseInt(o.get("showOrder").toString()))));
        models.addAll(pair.getFirst());
        relations.addAll(pair.getSecond());
    }

    @Override
    public PageBean<Map<String, Object>> page(String modelCode, PageParam pageParam) {
        int pageIndex = pageParam.getPageIndex();
        int pageSize = pageParam.getPageSize();
        String sortBy = pageParam.getSortBy();
        Boolean asc = pageParam.getAsc();
        String keyword = pageParam.getKeyword();

        ModelDto modelDto = modelService.getByCode(modelCode);
        List<ModelFieldDto> modelFieldDtos = modelFieldService.listByModelId(modelDto.getId());

        Criteria criteria = new Criteria();
        if (keyword != null) {
            List<Criteria> criterias = new ArrayList<>();
            modelFieldDtos.stream()
                    .filter(modelFieldDto -> Boolean.TRUE.equals(modelFieldDto.getSearchKey()))
                    .forEach(modelFieldDto -> {
                        DataType dataType = DataType.of(modelFieldDto.getDataType());
                        switch (dataType) {
                            case INT:
                                try {
                                    int val = Integer.parseInt(keyword);
                                    criterias.add(Criteria.where(modelFieldDto.getCode()).is(val));
                                } catch (NumberFormatException e) {
                                    log.error(e.getMessage(), e);
                                }
                                break;
                            case DOUBLE:
                                try {
                                    double val = Double.parseDouble(keyword);
                                    criterias.add(Criteria.where(modelFieldDto.getCode()).is(val));
                                } catch (NumberFormatException e) {
                                    log.error(e.getMessage(), e);
                                }
                                break;
                            case ENUM:
                            case TEXT:
                            case VARCHAR:
                                criterias.add(Criteria.where(modelFieldDto.getCode()).regex(keyword));
                        }
                    });
            if (!criterias.isEmpty()) {
                criteria.orOperator(criterias.toArray(new Criteria[0]));
            }
        }
        return super.page(modelCode, pageIndex, pageSize, sortBy, asc, criteria);
    }

    @Override
    public Map<String, Object> save(String model, Map<String, Object> params) {
        Map<String, Object> formatParams = formatParams(model, params);
        checkUniqueness(null, model, formatParams);
        Map<String, Object> entity = super.saveEntity(model, formatParams);

        entitySearchSender.saveOrUpdateEntity(model, entity.get("id").toString());

        return entity;
    }

    @Override
    public Map<String, Object> update(String model, String id, Map<String, Object> params) {
        Map<String, Object> formatParams = formatParams(model, params);
        checkUniqueness(id, model, formatParams);
        Map<String, Object> entity = super.updateEntity(model, id, formatParams);

        entitySearchSender.saveOrUpdateEntity(model, id);

        return entity;
    }

    @Override
    public void delete(String model, String... ids) {
        for (String id : ids) {
            entityRelationService.listByEntityId(id).stream()
                    .findAny()
                    .ifPresent(relation -> {
                        throw new InUsedException(ResourceType.Entity, id, ResourceType.EntityRelation,
                                relation.getId());
                    });
            entityRelationService.listByTargetEntityId(id).stream()
                    .findAny()
                    .ifPresent(relation -> {
                        throw new InUsedException(ResourceType.Entity, id, ResourceType.EntityRelation,
                                relation.getId());
                    });
            super.deleteEntity(model, id);

            entitySearchSender.deleteEntity(model, id);
        }
    }

    @Override
    public Map<String, Object> getById(String model, String id) {
        return super.getEntityById(model, id).
                orElseThrow(() -> new ResourceNotFoundException(ResourceType.Entity, id));
    }

    @Override
    public List<Map<String, Object>> list(String model, Query query) {
        return super.listEntities(model, query);
    }

    @Override
    public List<Map<String, Object>> listAll(String model) {
        return super.listAllEntities(model);
    }

    private Map<String, Object> formatParams(String model, Map<String, Object> params) {
        ModelDto modelDto = modelService.getByCode(model);
        Map<String, Object> result = new LinkedHashMap<>();
        params.forEach((key, value) -> {
            if (value != null && value.toString().length() > 0) {
                ModelFieldDto field = modelFieldService.getByCode(modelDto.getId(), key);
                DataType dataType = DataType.of(field.getDataType());
                switch (dataType) {
                    case DATETIME:
                    case DATE:
                        Instant instant = Instant.parse(value.toString());
                        value = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).withNano(0);
                }
            }
            result.put(key, value);
        });
        return result;
    }

    private void checkUniqueness(String entityId, String modelCode, Map<String, Object> params) {
        ModelDto model = modelService.getByCode(modelCode);
        List<ModelUniquenessDto> uniqs = modelUniquenessService.listByModelId(model.getId());
        uniqs.stream()
                .map(ModelUniquenessDto::getItems)
                .map(items -> items.stream()
                        .filter(item -> params.get(item.getModelFieldCode()) != null)
                        .map(item -> PropBean.of(item.getModelFieldCode(), params.get(item.getModelFieldCode()),
                                item.getModelFieldName()))
                        .collect(Collectors.toList()))
                .filter(props -> props.size() > 0)
                .forEach(props -> {
                    List<Map<String, Object>> entities = super.listEntities(modelCode, props.toArray(new PropBean[0]));
                    entities.stream()
                            .filter(e -> entityId == null || !e.get("id").equals(entityId))
                            .findAny()
                            .ifPresent(e -> {
                                StringBuilder sb = new StringBuilder();
                                for (PropBean prop : props) {
                                    if (sb.length() > 0) {
                                        sb.append(",");
                                    }
                                    sb.append(prop.getText());
                                }
                                throw new AlreadyExistsException(sb.toString());
                            });
                });
    }
}
