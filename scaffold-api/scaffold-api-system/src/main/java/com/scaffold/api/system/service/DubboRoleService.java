package com.scaffold.api.system.service;

import com.scaffold.api.system.domain.vo.DubboRoleVo;

import java.util.List;
import java.util.Set;

/**
 * 角色服务接口（Dubbo RPC）。
 *
 * <p>提供给其他微服务调用的角色查询接口。</p>
 *
 *  @author 8800
 */
public interface DubboRoleService {

    /**
     * 根据角色ID查询角色信息。
     *
     * @param roleId 角色ID
     * @return 角色信息，不存在返回 null
     */
    DubboRoleVo getRoleById(Long roleId);

    /**
     * 根据角色标识查询角色信息。
     *
     * @param roleKey 角色标识
     * @return 角色信息，不存在返回 null
     */
    DubboRoleVo getRoleByKey(String roleKey);

    /**
     * 根据角色ID列表批量查询角色信息。
     *
     * @param roleIds 角色ID集合
     * @return 角色信息列表
     */
    List<DubboRoleVo> getRolesByIds(Set<Long> roleIds);

    /**
     * 查询所有正常状态的角色。
     *
     * @return 角色信息列表
     */
    List<DubboRoleVo> getAllRoles();

    /**
     * 根据用户ID查询该用户拥有的角色列表。
     *
     * @param userId 用户ID
     * @return 角色信息列表
     */
    List<DubboRoleVo> getRolesByUserId(Long userId);

    /**
     * 根据角色ID查询绑定的菜单ID列表。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);
}
