package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.model.dto.LayerDto;
import com.eking.momp.model.param.LayerParam;

public interface LayerService {

	List<LayerDto> listWithModels(String keyword);

	List<LayerDto> list();

	LayerDto getById(Integer id);

	LayerDto save(LayerParam param);

	LayerDto update(Integer id, LayerParam param);

	void delete(Integer id);

}
