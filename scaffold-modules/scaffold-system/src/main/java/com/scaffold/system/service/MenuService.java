package com.scaffold.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scaffold.system.domain.entity.MenuEntity;
import com.scaffold.system.domain.vo.MenuVo;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限 Service 接口
 *
 * @author scaffold
 */
public interface MenuService extends IService<MenuEntity> {

    /**
     * 根据角色 ID 列表查询对应的菜单权限列表。
     *
     * @param roleIds 角色 ID 集合
     * @return 菜单权限列表
     */
    List<MenuEntity> selectMenuListByRoleIds(Set<Long> roleIds);

    /**
     * 根据角色 ID 列表查询对应的权限标识集合（perms）。
     *
     * @param roleIds 角色 ID 集合
     * @return 权限标识集合
     */
    Set<String> selectMenuPermsByRoleIds(Set<Long> roleIds);

    /**
     * 根据用户 ID 查询该用户拥有的菜单列表（用于动态路由构建）。
     *
     * @param userId 用户 ID
     * @return 菜单完整列表
     */
    List<MenuVo> selectMenuTreeByUserId(Long userId);

    /**
     * 根据用户 ID 查询该用户的权限标识列表。
     *
     * @param userId 用户 ID
     * @return 权限标识集合
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色 ID 查询菜单 ID 列表。
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    List<Long> selectMenuListByRoleId(Long roleId);
}
