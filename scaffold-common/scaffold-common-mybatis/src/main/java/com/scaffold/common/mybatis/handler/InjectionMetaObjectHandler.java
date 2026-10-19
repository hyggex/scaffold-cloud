package com.scaffold.common.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.scaffold.common.mybatis.core.domain.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * MyBatis Plus 字段自动填充处理器。
 *
 * <p>插入时自动填充：createTime、updateTime、createBy、updateBy、createDept。
 * 更新时自动填充：updateTime、updateBy。</p>
 *
 * <p>通过 Sa-Token 反射获取当前登录用户 ID，若未登录则使用默认值 -1L。
 * 反射调用可避免 scaffold-common-mybatis 与 Sa-Token 直接依赖。</p>
 */
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    /** 默认用户 ID（未登录时使用） */
    private static final Long DEFAULT_USER_ID = -1L;

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            Long userId = getCurrentUserId();
            Date current = new Date();

            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                if (ObjectUtil.isNull(baseEntity.getCreateBy())) {
                    baseEntity.setCreateBy(userId);
                    baseEntity.setUpdateBy(userId);
                    baseEntity.setCreateDept(userId);
                }
                if (ObjectUtil.isNull(baseEntity.getCreateTime())) {
                    baseEntity.setCreateTime(current);
                }
                if (ObjectUtil.isNull(baseEntity.getUpdateTime())) {
                    baseEntity.setUpdateTime(current);
                }
            } else {
                strictInsertFill(metaObject, "createTime", Date.class, current);
                strictInsertFill(metaObject, "updateTime", Date.class, current);
                strictInsertFill(metaObject, "createBy", Long.class, userId);
                strictInsertFill(metaObject, "updateBy", Long.class, userId);
                strictInsertFill(metaObject, "createDept", Long.class, userId);
            }
        } catch (Exception e) {
            log.warn("自动填充异常: {}", e.getMessage());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            Long userId = getCurrentUserId();
            Date current = new Date();

            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                baseEntity.setUpdateTime(current);
                if (ObjectUtil.isNull(baseEntity.getUpdateBy())) {
                    baseEntity.setUpdateBy(userId);
                }
            } else {
                strictUpdateFill(metaObject, "updateTime", Date.class, current);
                strictUpdateFill(metaObject, "updateBy", Long.class, userId);
            }
        } catch (Exception e) {
            log.warn("自动填充异常: {}", e.getMessage());
        }
    }

    /**
     * 通过反射调用 Sa-Token 获取当前登录用户 ID。
     * 若无法获取（未登录或 Sa-Token 不存在），返回默认值 -1L。
     */
    private Long getCurrentUserId() {
        try {
            Class<?> stpUtilClass = Class.forName("cn.dev33.satoken.stp.StpUtil");
            Method getLoginIdDefaultNull = stpUtilClass.getMethod("getLoginIdDefaultNull");
            Object loginId = getLoginIdDefaultNull.invoke(null);
            if (loginId != null) {
                if (loginId instanceof Long) {
                    return (Long) loginId;
                }
                return Long.valueOf(loginId.toString());
            }
        } catch (Exception ignored) {
            // Sa-Token 未引入或用户未登录
        }
        return DEFAULT_USER_ID;
    }
}
