package com.scaffold.common.security.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 登录辅助工具类。
 *
 * <p>在业务代码中获取当前登录用户信息的主要入口。</p>
 *
 * <p>使用示例：</p>
 * <pre>
 * // 获取当前登录用户 ID
 * Long userId = LoginHelper.getUserId();
 *
 * // 获取当前登录用户信息
 * LoginUser loginUser = LoginHelper.getLoginUser();
 *
 * // 获取当前用户部门 ID
 * Long deptId = LoginHelper.getDeptId();
 *
 * // 判断是否超级管理员
 * boolean isAdmin = LoginHelper.isSuperAdmin();
 *
 * // 判断是否有某个角色
 * boolean hasAdmin = LoginHelper.hasRole("admin");
 *
 * // 判断是否有某个权限
 * boolean hasPerms = LoginHelper.hasPermission("system:user:list");
 * </pre>
 *
 * @author 8800
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    /** 登录用户 Session Key */
    public static final String LOGIN_USER_KEY = "loginUser";

    /**
     * 获取当前登录用户的 ID。
     *
     * @return 用户 ID，未登录返回 null
     */
    public static Long getUserId() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof Long) {
            return (Long) loginId;
        }
        return Long.valueOf(loginId.toString());
    }

    /**
     * 获取当前登录用户的账号名。
     *
     * @return 用户账号，未登录返回 null
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }

    /**
     * 获取当前登录用户的部门 ID。
     *
     * @return 部门 ID，未登录返回 null
     */
    public static Long getDeptId() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getDeptId() : null;
    }

    /**
     * 获取当前登录用户的部门名称。
     *
     * @return 部门名称，未登录返回 null
     */
    public static String getDeptName() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getDeptName() : null;
    }

    /**
     * 获取当前登录用户的角色 ID 集合。
     *
     * @return 角色 ID 集合，未登录返回空集合
     */
    @SuppressWarnings("unchecked")
    public static Set<Long> getRoleIds() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRoleIds() : Set.of();
    }

    /**
     * 获取当前登录用户的角色 key 集合。
     *
     * @return 角色 key 集合，未登录返回空集合
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getRoleKeys() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getRoleKeys() : Set.of();
    }

    /**
     * 获取当前登录用户的权限标识集合。
     *
     * @return 权限标识集合，未登录返回空集合
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getPermissions() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null ? loginUser.getPermissions() : Set.of();
    }

    /**
     * 获取当前登录用户完整信息。
     *
     * @return LoginUser 对象，未登录返回 null
     */
    public static LoginUser getLoginUser() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        return (LoginUser) StpUtil.getSession().get(LOGIN_USER_KEY);
    }

    /**
     * 根据 Token 获取登录用户信息（用于 Dubbo RPC 远程调���）。
     *
     * @param token 认证 Token
     * @return LoginUser 对象，Token 无效返回 null
     */
    public static LoginUser getLoginUser(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        try {
            // 验证 Token 是否有效
            Boolean isLogin = (Boolean) StpUtil.isLogin(token);
            if (isLogin == null || !isLogin) {
                return null;
            }
            Object loginId = StpUtil.getLoginIdByToken(token);
            // 获取会话并获取 LoginUser
            SaSession session = StpUtil.getSessionByLoginId(loginId);
            return (LoginUser) session.get(LOGIN_USER_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前登录用户信息，如果未登录则抛出异常。
     *
     * @return LoginUser 对象
     * @throws IllegalStateException 未登录时抛出
     */
    public static LoginUser requireLoginUser() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            throw new IllegalStateException("用户未登录");
        }
        return loginUser;
    }

    /**
     * 判断当前用户是否为超级管理员（userId == 1）。
     *
     * @return 是否超级管理员
     */
    public static boolean isSuperAdmin() {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && loginUser.isSuperAdmin();
    }

    /**
     * 判断当前用户是否拥有指定角色。
     *
     * @param roleKey 角色 key（role_key 字段）
     * @return 是否拥有该角色
     */
    public static boolean hasRole(String roleKey) {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && loginUser.hasRole(roleKey);
    }

    /**
     * 判断当前用户是否拥有指定权限。
     *
     * @param permission 权限标识（菜单 perms 字段）
     * @return 是否拥有该权限
     */
    public static boolean hasPermission(String permission) {
        LoginUser loginUser = getLoginUser();
        return loginUser != null && loginUser.hasPermission(permission);
    }

    /**
     * 判断指定用户是否拥有指定角色（用于 Dubbo RPC 远程调用）。
     *
     * @param userId   用户ID
     * @param roleKey 角色 key
     * @return 是否拥有该角色
     */
    public static boolean hasRole(Long userId, String roleKey) {
        if (userId == null || roleKey == null) {
            return false;
        }
        // 超级管理员拥有所有角色
        if (userId == 1L) {
            return true;
        }
        // 此方法需要在 Dubbo 调用时由调用方传入用户完整信息
        // 这里仅做基础判断，具体逻辑由调用方处理
        return false;
    }

    /**
     * 判断指定用户是否拥有指定权限（用于 Dubbo RPC 远程调用）。
     *
     * @param userId      用户ID
     * @param permission 权限标识
     * @return 是否拥有该权限
     */
    public static boolean hasPermission(Long userId, String permission) {
        if (userId == null || permission == null) {
            return false;
        }
        // 超级管理员拥有所有权限
        if (userId == 1L) {
            return true;
        }
        // 此方法需要在 Dubbo 调用时由调用方传入用户完整信息
        // 这里仅做基础判断，具体逻辑由调用方处理
        return false;
    }

    /**
     * 判断指定用户是否为超级管理员。
     *
     * @param userId 用户ID
     * @return 是否超级管理员
     */
    public static boolean isSuperAdmin(Long userId) {
        return userId != null && userId == 1L;
    }

    /**
     * 将登录用户信息存入 Sa-Token Session。
     * 通常在登录时由 AuthServiceImpl 调用。
     *
     * @param loginUser 登录用户信息
     */
    public static void setLoginUser(LoginUser loginUser) {
        StpUtil.getSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 从 Sa-Token Session 中移除登录用户信息。
     * 通常在退出登录时调用。
     */
    public static void removeLoginUser() {
        if (StpUtil.isLogin()) {
            StpUtil.getSession().delete(LOGIN_USER_KEY);
        }
    }
}
