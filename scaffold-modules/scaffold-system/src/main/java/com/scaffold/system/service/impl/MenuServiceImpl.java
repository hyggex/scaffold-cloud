package com.scaffold.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffold.common.core.constant.SystemConstants;
import com.scaffold.common.core.utils.StringUtils;
import com.scaffold.system.domain.entity.MenuEntity;
import com.scaffold.system.domain.vo.MenuVo;
import com.scaffold.system.mapper.MenuMapper;
import com.scaffold.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单权限 Service 实现
 *
 *  @author 8800
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<MenuEntity> selectMenuListByRoleIds(Set<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return List.of();
        }
        LambdaQueryWrapper<MenuEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MenuEntity::getStatus, SystemConstants.NORMAL);
        lqw.inSql(MenuEntity::getMenuId,
            "SELECT m.menu_id FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "WHERE rm.role_id IN (" + StringUtils.join(roleIds, ",") + ")");
        return list(lqw);
    }

    @Override
    public Set<String> selectMenuPermsByRoleIds(Set<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Set.of();
        }
        String ids = StringUtils.join(roleIds, ",");
        LambdaQueryWrapper<MenuEntity> lqw = new LambdaQueryWrapper<>();
        lqw.eq(MenuEntity::getStatus, SystemConstants.NORMAL);
        lqw.inSql(MenuEntity::getMenuId,
            "SELECT m.menu_id FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "WHERE rm.role_id IN (" + ids + ")");
        lqw.isNotNull(MenuEntity::getPerms);
        lqw.ne(MenuEntity::getPerms, "");
        return list(lqw).stream()
            .map(MenuEntity::getPerms)
            .filter(s -> StringUtils.isNotBlank((CharSequence) s))
            .flatMap(perms -> Arrays.stream(perms.split(StringUtils.SEP)))
            .filter(s -> StringUtils.isNotBlank((CharSequence) s))
            .collect(Collectors.toSet());
    }

    @Override
    public List<MenuVo> selectMenuTreeByUserId(Long userId) {
        List<MenuEntity> menuList;
        // 超级管理员拥有所有菜单
        if (userId != null && userId == 1L) {
            LambdaQueryWrapper<MenuEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(MenuEntity::getStatus, SystemConstants.NORMAL);
            lqw.orderByAsc(MenuEntity::getParentId, MenuEntity::getOrderNum);
            menuList = list(lqw);
        } else {
            // 普通用户根据角色查询菜单
            menuList = menuMapper.selectMenuTreeByUserId(userId);
        }
        return buildMenuTree(convertTo(menuList));
    }

    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        if (userId != null && userId == 1L) {
            // 超级管理员拥有所有权限
            LambdaQueryWrapper<MenuEntity> lqw = new LambdaQueryWrapper<>();
            lqw.eq(MenuEntity::getStatus, SystemConstants.NORMAL);
            lqw.isNotNull(MenuEntity::getPerms);
            lqw.ne(MenuEntity::getPerms, "");
            return list(lqw).stream()
                .map(MenuEntity::getPerms)
                .filter(s -> StringUtils.isNotBlank((CharSequence) s))
                .flatMap(perms -> Arrays.stream(perms.split(StringUtils.SEP)))
                .filter(s -> StringUtils.isNotBlank((CharSequence) s))
                .collect(Collectors.toSet());
        }
        return menuMapper.selectMenuPermsByUserId(userId);
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        LambdaQueryWrapper<MenuEntity> lqw = new LambdaQueryWrapper<>();
        lqw.inSql(MenuEntity::getMenuId,
            "SELECT menu_id FROM sys_role_menu WHERE role_id = " + roleId);
        return list(lqw).stream().map(MenuEntity::getMenuId).collect(Collectors.toList());
    }

    /**
     * 将 MenuEntity 列表转换为 MenuVo 列表
     */
    private List<MenuVo> convertTo(List<MenuEntity> list) {
        return list.stream().map(entity -> {
            MenuVo vo = new MenuVo();
            vo.setMenuId(entity.getMenuId());
            vo.setParentId(entity.getParentId());
            vo.setMenuName(entity.getMenuName());
            vo.setOrderNum(entity.getOrderNum());
            vo.setPath(entity.getPath());
            vo.setComponent(entity.getComponent());
            vo.setQueryParam(entity.getQueryParam());
            vo.setIsFrame(entity.getIsFrame());
            vo.setIsCache(entity.getIsCache());
            vo.setMenuType(entity.getMenuType());
            vo.setVisible(entity.getVisible());
            vo.setStatus(entity.getStatus());
            vo.setPerms(entity.getPerms());
            vo.setIcon(entity.getIcon());
            vo.setRemark(entity.getRemark());
            vo.setCreateTime(entity.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 构建菜单树（父子结构）
     */
    private List<MenuVo> buildMenuTree(List<MenuVo> menus) {
        if (CollUtil.isEmpty(menus)) {
            return List.of();
        }
        // 按 parentId 分组
        Map<Long, List<MenuVo>> childrenMap = menus.stream()
            .filter(m -> m.getParentId() != null && m.getParentId() != 0L)
            .collect(Collectors.groupingBy(MenuVo::getParentId));

        menus.forEach(m -> {
            List<MenuVo> children = childrenMap.get(m.getMenuId());
            if (children != null) {
                children.sort(Comparator.comparing(MenuVo::getOrderNum));
                m.setChildren(children);
            } else {
                m.setChildren(new ArrayList<>());
            }
        });

        // 只返回根节点（parentId == 0）
        return menus.stream()
            .filter(m -> m.getParentId() == null || m.getParentId() == 0L)
            .sorted(Comparator.comparing(MenuVo::getOrderNum))
            .collect(Collectors.toList());
    }
}
