package com.eking.momp.db.mapper;

import com.eking.momp.db.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Dean
 * @since 2019-09-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
