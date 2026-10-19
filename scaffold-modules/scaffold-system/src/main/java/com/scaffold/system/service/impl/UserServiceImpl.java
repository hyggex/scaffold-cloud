package com.scaffold.system.service.impl;

import com.scaffold.common.mybatis.core.BaseServiceImpl;
import com.scaffold.system.domain.entity.UserEntity;
import com.scaffold.system.mapper.UserMapper;
import com.scaffold.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统用户业务实现。
 */
@Slf4j
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, UserEntity> implements UserService {

    /**
     * 批量创建用户。
     *
     * <p>每批5000条，插入后返回的对象列表中每个对象均已包含自动生成的ID和时间戳。</p>
     *
     * @param list 用户列表
     * @return 插入后的用户列表（含ID、时间戳等完整字段），与入参为同一引用
     */
    public List<UserEntity> batchCreate(List<UserEntity> list) {
        if (list == null || list.isEmpty()) {
            return list;
        }
        // 每批5000条，插入后返回完整对象列表
        return saveBatchAndReturn(list, 5000);
    }
}
