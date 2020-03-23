package com.eking.momp.model.service;

import java.util.List;

public interface DimensionRelationService {
	List<Integer> listModelRelationIdsByDimensionId(Integer dimensionId);
}
