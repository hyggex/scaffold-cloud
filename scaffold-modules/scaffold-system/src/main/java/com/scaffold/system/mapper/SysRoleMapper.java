package com.scaffold.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffold.system.domain.entity.RoleEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * 角色 Mapper
 *
 *  @author 8800
 */
public interface SysRoleMapper extends BaseMapper<RoleEntity> {

    /**
     * 根据用户 ID 查询该用户拥有的角色 ID 集合。
     */
    @Select("SELECT r.role_id FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = '0'")
    Set<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 查询该用户拥有的角色 key 集合。
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = '0'")
    Set<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 查询该用户拥有的角色名称集合。
     */
    @Select("SELECT r.role_name FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = '0'")
    Set<String> selectRoleNamesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色 ID 列表查询角色列表。
     */
    @Select("<script>" +
            "SELECT * FROM sys_role WHERE role_id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}</foreach> AND status = '0'" +
            "</script>")
    List<RoleEntity> selectRoleListByIds(@Param("roleIds") List<Long> roleIds);
}
