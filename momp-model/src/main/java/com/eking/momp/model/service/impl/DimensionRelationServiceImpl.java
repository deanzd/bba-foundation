package com.eking.momp.model.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.eking.momp.common.bean.PropBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eking.momp.db.mapper.DimensionRelationMapper;
import com.eking.momp.db.model.DimensionRelation;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.service.DimensionRelationService;

@Service
@Transactional
public class DimensionRelationServiceImpl extends AbstractService<DimensionRelationMapper, DimensionRelation>
		implements DimensionRelationService {

	@Override
	public List<Integer> listModelRelationIdsByDimensionId(Integer dimensionId) {
		return super.listObjByProps(PropBean.of("dimension_id", dimensionId)).stream()
				.map(DimensionRelation::getModelRelationId)
				.collect(Collectors.toList());
	}
}
