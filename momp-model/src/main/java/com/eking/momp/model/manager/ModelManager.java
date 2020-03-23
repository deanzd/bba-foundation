package com.eking.momp.model.manager;

import com.eking.momp.model.dto.ModelDto;
import com.eking.momp.model.dto.ModelRelationTopoDto;
import com.eking.momp.model.dto.ModelTopoDto;
import com.eking.momp.model.dto.TopoDto;

import java.util.List;

public interface ModelManager {

    TopoDto<List<ModelTopoDto>, List<ModelRelationTopoDto>> getModelTopo(Integer modelId);

    List<ModelDto> list(String keyword);

    void clearLayerId(Integer layerId);
}
