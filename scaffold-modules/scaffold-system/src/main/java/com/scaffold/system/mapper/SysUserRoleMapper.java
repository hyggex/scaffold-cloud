package com.scaffold.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffold.system.domain.entity.userrole.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户角色关联 Mapper
 *
 * @author scaffold
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 批量插入用户角色关联。
     */
    int insertUserRoles(@Param("list") List<SysUserRole> userRoles);

    /**
     * 根据用户 ID 删除用户角色关联。
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 删除角色用户关联。
     */
    int deleteByRoleId(@Param("roleId") Long roleId);
}
