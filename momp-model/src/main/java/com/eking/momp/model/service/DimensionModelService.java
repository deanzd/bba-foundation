package com.eking.momp.model.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.eking.momp.db.model.DimensionModel;

public interface DimensionModelService {

	List<DimensionModel> list(QueryWrapper<DimensionModel> qw);

}
