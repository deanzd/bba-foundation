package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.model.dto.ModelUniquenessDto;
import com.eking.momp.model.param.ModelUniquenessParam;

public interface ModelUniquenessService {

	List<ModelUniquenessDto> listByModelId(Integer modelId);

	ModelUniquenessDto getById(Integer id);

	ModelUniquenessDto save(ModelUniquenessParam param);
	
	ModelUniquenessDto update(Integer id, ModelUniquenessParam param);
	
	void delete(Integer id);
	
}
