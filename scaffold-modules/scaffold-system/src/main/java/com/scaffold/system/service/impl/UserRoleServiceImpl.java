package com.scaffold.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.scaffold.system.domain.entity.userrole.SysUserRole;
import com.scaffold.system.mapper.SysUserRoleMapper;
import com.scaffold.system.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联 Service 实现
 *
 * @author scaffold
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final SysUserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除现有关联
        userRoleMapper.deleteByUserId(userId);
        // 再新增
        if (CollUtil.isNotEmpty(roleIds)) {
            List<SysUserRole> userRoles = roleIds.stream()
                .map(roleId -> new SysUserRole(userId, roleId))
                .collect(Collectors.toList());
            userRoleMapper.insertUserRoles(userRoles);
        }
    }

    @Override
    public List<Long> selectRoleIdsByUserId(Long userId) {
        return userRoleMapper.selectList(null).stream()
            .filter(ur -> ur.getUserId().equals(userId))
            .map(SysUserRole::getRoleId)
            .collect(Collectors.toList());
    }

    @Override
    public int insertUserRoles(List<SysUserRole> userRoles) {
        return userRoleMapper.insertUserRoles(userRoles);
    }

    @Override
    public int deleteByUserId(Long userId) {
        return userRoleMapper.deleteByUserId(userId);
    }
}
