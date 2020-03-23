package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.model.dto.DimensionDto;

public interface DimensionService {

	DimensionDto getById(Integer dimensionId);

	List<DimensionDto> listSysInit();
	
}
