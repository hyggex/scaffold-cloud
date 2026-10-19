package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 登录用户信息（Dubbo RPC 传输对象）。
 *
 * <p>包含用户基本信息、角色、权限等，供其他微服务查询使用。</p>
 *
 *  @author 8800
 */
@Data
public class DubboLoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // ==================== 用户基本信息 ====================

    /** 用户ID */
    private Long userId;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 用户类型 */
    private String userType;

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phonenumber;

    /** 用户性别（0男 1女 2未知） */
    private String sex;

    /** 头像地址 */
    private String avatar;

    /** 账号状态（0正常 1停用） */
    private String status;

    // ==================== 角色权限信息 ====================

    /** 角色ID列表 */
    private Set<Long> roleIds;

    /** 角色标识列表（如：admin） */
    private Set<String> roleKeys;

    /** 角色名称列表 */
    private Set<String> roleNames;

    /** 权限标识列表（如：system:user:list） */
    private Set<String> permissions;

    /** 是否超级管理员 */
    private boolean superAdmin;

    // ==================== 登录信息 ====================

    /** 登录Token */
    private String token;

    /** 登录IP */
    private String loginIp;

    /** 登录时间 */
    private LocalDateTime loginDate;
}
