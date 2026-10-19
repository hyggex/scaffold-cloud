package com.scaffold.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffold.system.domain.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户数据访问层。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
}
