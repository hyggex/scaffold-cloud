package com.scaffold.common.core.constant;

/**
 * 系统常量
 *
 * @author Lion Li
 */
public interface SystemConstants {

    /** UTF-8 字符集 */
    String UTF8 = "UTF-8";

    /** 根部门 ID */
    Long ROOT_DEPT_ID = 0L;

    /** 根部门祖级列表 */
    String ROOT_DEPT_ANCESTORS = "0";

    /** 超级管理员用户 ID */
    Long SUPER_ADMIN_ID = 1L;

    /** 超级管理员角色 ID */
    Long SUPER_ADMIN_ROLE_ID = 1L;

    /** 超级管理员角色 key */
    String SUPER_ADMIN_ROLE_KEY = "admin";

    /** 分隔符 */
    String SEPARATOR = ",";

    /** 删除标志（正常） */
    String NORMAL = "0";

    /** 删除标志（删除） */
    String DELETED = "1";

    /** 部门正常状态 */
    String DEPT_NORMAL = "0";

    /** 部门停用状态 */
    String DEPT_DISABLE = "1";

    /** 用户正常状态 */
    String USER_NORMAL = "0";

    /** 用户停用状态 */
    String USER_DISABLE = "1";

    /** 角色正常状态 */
    String ROLE_NORMAL = "0";

    /** 角色停用状态 */
    String ROLE_DISABLE = "1";

    /** 菜单正常状态 */
    String MENU_NORMAL = "0";

    /** 菜单停用状态 */
    String MENU_DISABLE = "1";

    /** 部门树选择项是否关联显示 */
    boolean DEPT_CHECK_STRICTLY = true;

    /** 菜单树选择项是否关联显示 */
    boolean MENU_CHECK_STRICTLY = true;

    /** 是否唯一标识 */
    String UNIQUE = "0";

    /** 是否唯一标识（否） */
    String NOT_UNIQUE = "1";

    /** 数据权限范围 - 全部数据权限 */
    String DATA_SCOPE_ALL = "1";

    /** 数据权限范围 - 自定义数据权限 */
    String DATA_SCOPE_CUSTOM = "2";

    /** 数据权限范围 - 本部门数据权限 */
    String DATA_SCOPE_DEPT = "3";

    /** 数据权限范围 - 本部门及以下数据权限 */
    String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /** 数据权限范围 - 仅本人数据权限 */
    String DATA_SCOPE_SELF = "5";

    /** 默认分页大小 */
    int PAGE_SIZE = 10;

    /** 最大分页大小 */
    int MAX_PAGE_SIZE = 100;
}
