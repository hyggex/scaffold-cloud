package com.scaffold.common.security.util;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户信息。
 *
 * <p>包含用户基本信息、所属部门、角色、权限等，用于在请求上下文中传递已登录用户完整信息。
 * 登录时由 AuthServiceImpl 创建并存入 Sa-Token Session。</p>
 *
 *  @author 8800
 */
@Data
@NoArgsConstructor
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    private Long userId;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 部门 ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 角色 ID 集合 */
    private Set<Long> roleIds;

    /** 角色 key 集合（用于权限判断） */
    private Set<String> roleKeys;

    /** 角色名称集合 */
    private Set<String> roleNames;

    /** 权限标识集合（菜单 perms） */
    private Set<String> permissions;

    /** 登录 IP */
    private String loginIp;

    /** 登录日期 */
    private String loginDate;

    /** 账号状态（0 正常 1 停用） */
    private String status;

    /** 用户类型 */
    private String userType;

    public LoginUser(Long userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    /**
     * 判断是否为超级管理员。
     * 超级管理员 ID 为 1，不受数据权限限制。
     */
    public boolean isSuperAdmin() {
        return userId != null && userId == 1L;
    }

    /**
     * 判断是否拥有指定角色。
     */
    public boolean hasRole(String roleKey) {
        return roleKeys != null && roleKeys.contains(roleKey);
    }

    /**
     * 判断是否拥有指定权限。
     */
    public boolean hasPermission(String permission) {
        return permissions != null && (permissions.contains(permission) || permissions.contains("*"));
    }
}
