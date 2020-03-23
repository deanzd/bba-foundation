package com.eking.momp.model.service;

import java.util.List;

import com.eking.momp.model.dto.ModelUniquenessItemDto;
import com.eking.momp.model.param.ModelUniquenessItemParam;

public interface ModelUniquenessItemService {
	List<ModelUniquenessItemDto> listByModelUniquenessId(Integer modelUniquenessId);

	ModelUniquenessItemDto save(ModelUniquenessItemParam param);
	
	void delete(Integer id);

    List<ModelUniquenessItemDto> listByModelFieldId(Integer modelFieldId);
}
