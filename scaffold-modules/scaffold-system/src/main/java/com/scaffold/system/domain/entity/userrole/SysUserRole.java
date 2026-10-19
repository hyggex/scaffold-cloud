package com.scaffold.system.domain.entity.userrole;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.scaffold.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 用户和角色关联实体
 *
 * @author scaffold
 */
@Data
@NoArgsConstructor
@TableName("sys_user_role")
public class SysUserRole extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 角色 ID
     */
    private Long roleId;

    public SysUserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
