package com.scaffold.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffold.system.domain.entity.userrole.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联 Mapper
 *
 *  @author 8800
 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 批量插入角色菜单关联。
     */
    int insertRoleMenus(@Param("list") List<SysRoleMenu> roleMenus);

    /**
     * 根据角色 ID 删除角色菜单关联。
     */
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单 ID 删除角色菜单关联。
     */
    int deleteByMenuId(@Param("menuId") Long menuId);
}
