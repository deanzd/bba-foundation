package com.eking.momp.model.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.eking.momp.common.Constant;
import com.eking.momp.common.ResourceType;
import com.eking.momp.common.bean.PropBean;
import com.eking.momp.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eking.momp.db.mapper.DimensionMapper;
import com.eking.momp.db.model.Dimension;
import com.eking.momp.db.AbstractService;
import com.eking.momp.model.dto.DimensionDto;
import com.eking.momp.model.service.DimensionService;

@Service
@Transactional
public class DimensionServiceImpl extends AbstractService<DimensionMapper, Dimension> implements DimensionService {

    @Override
    public DimensionDto getById(Integer dimensionId) {
        return super.getObjById(dimensionId)
                .map(DimensionDto::new)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceType.Dimension, dimensionId));
    }

    @Override
    public List<DimensionDto> listSysInit() {
        List<Dimension> dimensions = super.listObjByProps(Constant.FIELD_SHOW_ORDER, true,
                PropBean.of(Constant.FIELD_SYS_INIT, true));
        return dimensions.stream()
                .map(DimensionDto::new)
                .collect(Collectors.toList());
    }

}
