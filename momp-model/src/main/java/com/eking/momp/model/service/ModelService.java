package com.eking.momp.model.service;

import com.eking.momp.db.model.Model;
import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.param.ModelParam;

import java.util.List;

public interface ModelService {

	List<ModelDto> list();

	ModelDto save(ModelParam param);

	ModelDto update(Integer id, ModelParam param);

	ModelDto update(Model model);

	Model delete(Integer id);

	ModelDto getById(Integer id);

	Model getForUpdate(Integer id);

	ModelDto getByCode(String modelCode);

    List<ModelDto> listByLayerId(Integer layerId);
}
