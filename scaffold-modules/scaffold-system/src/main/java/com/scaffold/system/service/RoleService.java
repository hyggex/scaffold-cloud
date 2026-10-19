package com.scaffold.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scaffold.system.domain.entity.RoleEntity;
import com.scaffold.system.domain.vo.RoleVo;

import java.util.List;
import java.util.Set;

/**
 * 角色管理 Service 接口
 *
 *  @author 8800
 */
public interface RoleService extends IService<RoleEntity> {

    /**
     * 根据角色 ID 查询角色信息。
     */
    RoleVo selectRoleById(Long roleId);

    /**
     * 根据角色 ID 列表查询角色信息。
     */
    List<RoleVo> selectRoleListByIds(Set<Long> roleIds);

    /**
     * 查询所有正常状态的角色列表。
     */
    List<RoleVo> selectRoleAll();

    /**
     * 根据用户 ID 查询该用户拥有的角色列表。
     */
    List<RoleVo> selectRoleListByUserId(Long userId);

    /**
     * 新增角色。
     */
    int insertRole(RoleEntity role, List<Long> menuIds);

    /**
     * 修改角色。
     */
    int updateRole(RoleEntity role, List<Long> menuIds);

    /**
     * 删除角色（物理删除）。
     */
    int deleteRoleById(Long roleId);

    /**
     * 修改角色状态。
     */
    int updateRoleStatus(Long roleId, String status);

    /**
     * 批量删除角色。
     */
    int deleteRoleByIds(List<Long> roleIds);
}
