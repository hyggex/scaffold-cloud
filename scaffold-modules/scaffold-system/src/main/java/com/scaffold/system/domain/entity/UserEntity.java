package com.scaffold.system.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.scaffold.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 用户实体。
 *
 *  @author 8800
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class UserEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户ID（雪花ID） */
    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    /** 所属部门ID */
    private Long deptId;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 用户类型（00=系统用户） */
    private String userType;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phonenumber;

    /** 用户性别（0=男 1=女 2=未知） */
    private String sex;

    /** 头像地址 */
    private String avatar;

    /** 密码（BCrypt加密存储） */
    private String password;

    /** 账号状态（0=正常 1=停用） */
    private String status;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginDate;

    /** 删除标志（0=存在 1=删除） */
    @TableLogic
    private String delFlag;

    /** 备注 */
    private String remark;
}
