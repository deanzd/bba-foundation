package com.eking.momp.model.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.db.model.ModelField;
import com.eking.momp.model.dto.ModelFieldDto;
import com.eking.momp.model.param.ModelFieldParam;

public interface ModelFieldService {
	
	ModelFieldDto getById(Integer id);

	ModelFieldDto save(ModelFieldParam param);

	ModelFieldDto update(Integer modelFieldId, ModelFieldParam param);

	void delete(Integer id);

	List<ModelFieldDto> list(QueryWrapper<ModelField> qw);

	ModelFieldDto getByCode(Integer modelId, String code);

	List<ModelFieldDto> listByModelId(Integer modelId);
}
