package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 部门信息（Dubbo RPC 传输对象）。
 *
 *  @author 8800
 */
@Data
public class DubboDeptVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 部门ID */
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 父部门名称 */
    private String parentName;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人ID */
    private Long leader;

    /** 负责人名称 */
    private String leaderName;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态（0正常 1停用） */
    private String status;

    /** 子部门 */
    private List<DubboDeptVo> children;
}
