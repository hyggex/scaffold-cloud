package com.scaffold.system.service;

import com.scaffold.system.domain.entity.userrole.SysUserRole;

import java.util.List;

/**
 * 用户角色关联 Service 接口
 *
 *  @author 8800
 */
public interface UserRoleService {

    /**
     * 批量分配角色给用户（先删后增）。
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 根据用户 ID 查询角色 ID 列表。
     */
    List<Long> selectRoleIdsByUserId(Long userId);

    /**
     * 批量插入用户角色关联。
     */
    int insertUserRoles(List<SysUserRole> userRoles);

    /**
     * 根据用户 ID 删除用户角色关联。
     */
    int deleteByUserId(Long userId);

    /**
     * 根据角色 ID 查询用户 ID 列表。
     */
    List<Long> selectUserIdsByRoleId(Long roleId);
}
