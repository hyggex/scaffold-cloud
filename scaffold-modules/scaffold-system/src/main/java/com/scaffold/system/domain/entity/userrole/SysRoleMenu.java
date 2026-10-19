package com.scaffold.system.domain.entity.userrole;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.scaffold.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 角色和菜单关联实体
 *
 * @author scaffold
 */
@Data
@NoArgsConstructor
@TableName("sys_role_menu")
public class SysRoleMenu extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 菜单 ID
     */
    private Long menuId;

    public SysRoleMenu(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
    }
}
