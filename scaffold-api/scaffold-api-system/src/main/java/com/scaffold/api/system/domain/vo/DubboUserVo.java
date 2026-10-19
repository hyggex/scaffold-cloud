package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息（Dubbo RPC 传输对象）。
 *
 *  @author 8800
 */
@Data
public class DubboUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 部门名称 */
    private String deptName;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 用户类型 */
    private String userType;

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

    /** 备注 */
    private String remark;
}
