package com.scaffold.api.system.service;

import com.scaffold.api.system.domain.vo.DubboMenuVo;

import java.util.List;
import java.util.Set;

/**
 * 菜单服务接口（Dubbo RPC）。
 *
 * <p>提供给其他微服务调用的菜单查询接口。</p>
 *
 *  @author 8800
 */
public interface DubboMenuService {

    /**
     * 根据菜单ID查询菜单信息。
     *
     * @param menuId 菜单ID
     * @return 菜单信息，不存在返回 null
     */
    DubboMenuVo getMenuById(Long menuId);

    /**
     * 根据用户ID查询该用户的菜单树（用于前端路由构建）。
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<DubboMenuVo> getMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询该角色的菜单权限标识列表。
     *
     * @param roleId 角色ID
     * @return 权限标识列表
     */
    Set<String> getMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询该用户的所有权限标识列表。
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    Set<String> getMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID列表查询所有角色的菜单ID列表。
     *
     * @param roleIds 角色ID集合
     * @return 菜单ID列表
     */
    List<Long> getMenuIdsByRoleIds(Set<Long> roleIds);
}
