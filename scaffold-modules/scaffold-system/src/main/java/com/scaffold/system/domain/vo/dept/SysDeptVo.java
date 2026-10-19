package com.scaffold.system.domain.vo.dept;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门视图对象
 *
 * @author scaffold
 */
@Data
@NoArgsConstructor
public class SysDeptVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 部门 ID */
    private Long deptId;

    /** 父部门 ID */
    private Long parentId;

    /** 父部门名称 */
    private String parentName;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private Long leader;

    /** 负责人名称 */
    private String leaderName;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态（0 正常 1 停用） */
    private String status;

    /** 子部门 */
    private List<SysDeptVo> children = new ArrayList<>();
}
