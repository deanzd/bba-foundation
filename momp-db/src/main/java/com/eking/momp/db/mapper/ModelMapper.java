package com.eking.momp.db.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eking.momp.db.model.Model;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Dean
 * @since 2019-06-12
 */
@Mapper
public interface ModelMapper extends BaseMapper<Model> {

}
