package com.scaffold.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffold.common.core.constant.SystemConstants;
import com.scaffold.system.domain.entity.MenuEntity;
import com.scaffold.system.domain.vo.MenuVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限 Mapper
 *
 * @author scaffold
 */
public interface MenuMapper extends BaseMapper<MenuEntity> {

    /**
     * 根据用户 ID 查询该用户的菜单树。
     */
    @Select("""
        SELECT DISTINCT m.* FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        INNER JOIN sys_role r ON ur.role_id = r.role_id
        WHERE ur.user_id = #{userId}
          AND m.status = '0'
          AND r.status = '0'
        ORDER BY m.parent_id, m.order_num
        """)
    List<MenuEntity> selectMenuTreeByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 查询该用户的权限标识集合。
     */
    @Select("""
        SELECT DISTINCT m.perms FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        INNER JOIN sys_role r ON ur.role_id = r.role_id
        WHERE ur.user_id = #{userId}
          AND m.status = '0'
          AND r.status = '0'
          AND m.perms IS NOT NULL AND m.perms != ''
        """)
    Set<String> selectMenuPermsByUserId(@Param("userId") Long userId);
}
