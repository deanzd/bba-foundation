package com.eking.momp.model.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.db.mapper.DimensionModelMapper;
import com.eking.momp.db.model.DimensionModel;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.service.DimensionModelService;

@Service
@Transactional
public class DimensionModelServiceImpl extends AbstractService<DimensionModelMapper, DimensionModel>
		implements DimensionModelService {

	@Override
	public List<DimensionModel> list(QueryWrapper<DimensionModel> qw) {
		return super.listObjs(qw);
	}
}
