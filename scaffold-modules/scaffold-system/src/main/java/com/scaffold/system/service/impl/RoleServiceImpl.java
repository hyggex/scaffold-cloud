package com.scaffold.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffold.common.core.constant.SystemConstants;
import com.scaffold.common.core.exception.ServiceException;
import com.scaffold.system.domain.entity.MenuEntity;
import com.scaffold.system.domain.entity.RoleEntity;
import com.scaffold.system.domain.entity.userrole.SysRoleMenu;
import com.scaffold.system.domain.entity.userrole.SysUserRole;
import com.scaffold.system.domain.vo.RoleVo;
import com.scaffold.system.mapper.SysRoleMapper;
import com.scaffold.system.mapper.SysRoleMenuMapper;
import com.scaffold.system.mapper.SysUserRoleMapper;
import com.scaffold.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色管理 Service 实现
 *
 *  @author 8800
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<SysRoleMapper, RoleEntity> implements RoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public RoleVo selectRoleById(Long roleId) {
        RoleEntity role = getById(roleId);
        if (role == null) {
            return null;
        }
        return convertToVo(role);
    }

    @Override
    public List<RoleVo> selectRoleListByIds(Set<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }
        return roleMapper.selectRoleListByIds(new ArrayList<>(roleIds))
            .stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<RoleVo> selectRoleAll() {
        LambdaQueryWrapper<RoleEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(RoleEntity::getStatus, SystemConstants.NORMAL);
        lqw.orderByAsc(RoleEntity::getRoleSort);
        return list(lqw).stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    public List<RoleVo> selectRoleListByUserId(Long userId) {
        Set<Long> roleIds = roleMapper.selectRoleIdsByUserId(userId);
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }
        return listByIds(roleIds).stream().map(this::convertToVo).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRole(RoleEntity role, List<Long> menuIds) {
        // 校验角色 key 唯一性
        long count = count(new LambdaQueryWrapper<RoleEntity>()
            .eq(RoleEntity::getRoleKey, role.getRoleKey()));
        if (count > 0) {
            throw new ServiceException("角色权限标识已存在");
        }

        // 设置默认值
        if (role.getDataScope() == null) {
            role.setDataScope(SystemConstants.DATA_SCOPE_ALL);
        }
        if (role.getMenuCheckStrictly() == null) {
            role.setMenuCheckStrictly(SystemConstants.MENU_CHECK_STRICTLY);
        }
        if (role.getDeptCheckStrictly() == null) {
            role.setDeptCheckStrictly(SystemConstants.DEPT_CHECK_STRICTLY);
        }

        boolean saved = save(role);
        if (!saved) {
            return 0;
        }

        // 保存角色菜单关联
        if (CollUtil.isNotEmpty(menuIds)) {
            List<SysRoleMenu> roleMenus = menuIds.stream()
                .map(menuId -> new SysRoleMenu(role.getRoleId(), menuId))
                .collect(Collectors.toList());
            roleMenuMapper.insertRoleMenus(roleMenus);
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRole(RoleEntity role, List<Long> menuIds) {
        // 校验角色 key 唯一性
        long count = count(new LambdaQueryWrapper<RoleEntity>()
            .eq(RoleEntity::getRoleKey, role.getRoleKey())
            .ne(RoleEntity::getRoleId, role.getRoleId()));
        if (count > 0) {
            throw new ServiceException("角色权限标识已存在");
        }

        boolean updated = updateById(role);
        if (!updated) {
            return 0;
        }

        // 更新角色菜单关联：先删后增
        roleMenuMapper.deleteByRoleId(role.getRoleId());
        if (CollUtil.isNotEmpty(menuIds)) {
            List<SysRoleMenu> roleMenus = menuIds.stream()
                .map(menuId -> new SysRoleMenu(role.getRoleId(), menuId))
                .collect(Collectors.toList());
            roleMenuMapper.insertRoleMenus(roleMenus);
        }
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleById(Long roleId) {
        // 校验是否有用户关联
        long userCount = userRoleMapper.selectCount(null); // 后续补充按角色查
        // 暂时直接删除
        roleMenuMapper.deleteByRoleId(roleId);
        return removeById(roleId) ? 1 : 0;
    }

    @Override
    public int updateRoleStatus(Long roleId, String status) {
        RoleEntity role = new RoleEntity();
        role.setRoleId(roleId);
        role.setStatus(status);
        return updateById(role) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRoleByIds(List<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return 0;
        }
        roleIds.forEach(roleId -> {
            roleMenuMapper.deleteByRoleId(roleId);
            userRoleMapper.deleteByRoleId(roleId);
        });
        return removeByIds(roleIds) ? roleIds.size() : 0;
    }

    private RoleVo convertToVo(RoleEntity role) {
        RoleVo vo = new RoleVo();
        vo.setRoleId(role.getRoleId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setRoleSort(role.getRoleSort());
        vo.setDataScope(role.getDataScope());
        vo.setMenuCheckStrictly(role.getMenuCheckStrictly());
        vo.setDeptCheckStrictly(role.getDeptCheckStrictly());
        vo.setStatus(role.getStatus());
        vo.setRemark(role.getRemark());
        vo.setCreateTime(role.getCreateTime());
        vo.setUpdateTime(role.getUpdateTime());
        return vo;
    }
}
