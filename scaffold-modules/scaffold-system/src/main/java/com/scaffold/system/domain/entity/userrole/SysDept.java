package com.scaffold.system.domain.entity.userrole;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.scaffold.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门实体。
 *
 * <p>对应表：sys_dept</p>
 *
 *  @author 8800
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long deptId;

    /**
     * 父部门 ID（0=顶级部门）
     */
    private Long parentId;

    /**
     * 祖级列表（格式：0,100,101）
     */
    private String ancestors;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 显示顺序
     */
    private Integer orderNum;

    /**
     * 部门负责人用户 ID
     */
    @TableField("leader_user_id")
    private Long leaderUserId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 部门状态（0=正常 1=停用）
     */
    private String status;

    /**
     * 删除标志（0=存在 1=删除）
     */
    @TableLogic
    private String delFlag;

    /** 父部门名称（非数据库字段） */
    @TableField(exist = false)
    private String parentName;

    /** 子部门列表（非数据库字段） */
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<>();
}
