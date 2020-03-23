package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.model.dto.ModelRelationDetailDto;
import com.eking.momp.model.dto.ModelRelationDto;
import com.eking.momp.model.param.ModelRelationParam;

public interface ModelRelationService {

	List<ModelRelationDetailDto> listDetailByModelId(Integer modelId);

	List<ModelRelationDetailDto> listDetailByTargetModelId(Integer targetModelId);
	
	List<ModelRelationDetailDto> listDetail();

	List<ModelRelationDto> listByModelId(Integer modelId);

	List<ModelRelationDto> listByTargetModelId(Integer targetModelId);
	
	List<ModelRelationDto> list();

	ModelRelationDto getById(Integer id);

	ModelRelationDetailDto getDetailById(Integer id);

	ModelRelationDto save(ModelRelationParam param);
	
	ModelRelationDto update(Integer id, ModelRelationParam param);
	
	void delete(Integer id);

	List<ModelRelationDto> listByRelationTypeId(Integer relationTypeId);
}
