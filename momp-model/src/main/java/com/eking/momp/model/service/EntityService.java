package com.eking.momp.model.service;

import java.util.List;
import java.util.Map;

import com.eking.momp.common.param.PageParam;
import com.eking.momp.model.dto.*;
import org.springframework.data.mongodb.core.query.Query;

import com.eking.momp.common.bean.PageBean;

import javax.servlet.http.HttpServletResponse;

public interface EntityService {

	ModelEntitiesDto getModelEntities(Integer dimensionId, String keyword);

	TopoDto<List<ModelEntitiesDto>, List<EntityRelationDto>> getTopo(String model, String entityId,
																	 Integer dimensionId);

	TopoDto<List<ModelEntitiesDto>, List<EntityRelationTopoDto>> getTopoLevel2(String model, String entityId,
																			   List<Integer> modelRelationIds, List<Integer> modelRelationIdsR);

	void export(String modelCode, String keyword, HttpServletResponse response);

	PageBean<EntityDto> search(String keyword, String lastId);


	Map<String, Object> save(String model, Map<String, Object> params);

	Map<String, Object> update(String model, String id, Map<String, Object> params);

	List<Map<String, Object>> list(String model, Query query);

	PageBean<Map<String, Object>> page(String model, PageParam pageParam);

	List<Map<String, Object>> listAll(String model);

	void delete(String model, String... id);

    Map<String, Object> getById(String model, String id);
}
