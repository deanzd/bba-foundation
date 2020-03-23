package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.model.dto.RelationTypeDto;
import com.eking.momp.model.param.RelationTypeParam;

public interface RelationTypeService {
	
	List<RelationTypeDto> list();

	RelationTypeDto getById(Integer id);

	RelationTypeDto save(RelationTypeParam param);
	
	RelationTypeDto update(Integer id, RelationTypeParam param);
	
	void delete(Integer id);
}
