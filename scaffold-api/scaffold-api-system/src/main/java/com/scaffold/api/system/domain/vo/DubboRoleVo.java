package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色信息（Dubbo RPC 传输对象）。
 *
 *  @author 8800
 */
@Data
public class DubboRoleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色ID */
    private Long roleId;

    /** 角色名称 */
    private String roleName;

    /** 角色标识 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 数据范围（1=全部数据 2=本部门数据 3=本部门及以下 4=仅本人数据 5=自定义） */
    private String dataScope;

    /** 角色状态（0正常 1停用） */
    private String status;

    /** 备注 */
    private String remark;
}
