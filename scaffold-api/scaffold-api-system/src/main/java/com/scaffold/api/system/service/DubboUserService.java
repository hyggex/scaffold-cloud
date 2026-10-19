package com.scaffold.api.system.service;

import com.scaffold.api.system.domain.vo.DubboUserVo;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口（Dubbo RPC）。
 *
 * <p>提供给其他微服务调用的用户查询接口。</p>
 *
 *  @author 8800
 */
public interface DubboUserService {

    /**
     * 根据用户ID查询用户信息。
     *
     * @param userId 用户ID
     * @return 用户信息，不存在返回 null
     */
    DubboUserVo getUserById(Long userId);

    /**
     * 根据用户名查询用户信息。
     *
     * @param username 用户名
     * @return 用户信息，不存在返回 null
     */
    DubboUserVo getUserByUsername(String username);

    /**
     * 根据用户ID列表批量查询用户信息。
     *
     * @param userIds 用户ID集合
     * @return 用户信息列表
     */
    List<DubboUserVo> getUsersByIds(Set<Long> userIds);

    /**
     * 根据部门ID查询该部门下的所有用户ID。
     *
     * @param deptId 部门ID
     * @return 用户ID列表
     */
    List<Long> getUserIdsByDeptId(Long deptId);

    /**
     * 根据角色ID查询拥有该角色的所有用户ID。
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> getUserIdsByRoleId(Long roleId);

    /**
     * 判断用户是否���级管理员。
     *
     * @param userId 用户ID
     * @return true 是，false 否
     */
    boolean isSuperAdmin(Long userId);

    /**
     * 判断用户是否拥有指定角色。
     *
     * @param userId 用户ID
     * @param roleKey 角色标识
     * @return true 拥有，false 不拥有
     */
    boolean hasRole(Long userId, String roleKey);

    /**
     * 判断用户是否拥有指定权限。
     *
     * @param userId 用户ID
     * @param permission 权限标识
     * @return true 拥有，false 不拥有
     */
    boolean hasPermission(Long userId, String permission);
}
