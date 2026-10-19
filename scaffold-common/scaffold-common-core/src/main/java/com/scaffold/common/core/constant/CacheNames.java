package com.scaffold.common.core.constant;

/**
 * 缓存名称常量
 *
 * @author 8800
 */
public interface CacheNames {

    /** 通用缓存前缀 */
    String CACHE = "cache:";

    /** 用户信息缓存 */
    String SYS_USER = "sys_user";

    /** 部门信息缓存 */
    String SYS_DEPT = "sys_dept";

    /** 部门及子部门缓存 */
    String SYS_DEPT_AND_CHILD = "sys_dept_and_child";

    /** 角色信息缓存 */
    String SYS_ROLE = "sys_role";

    /** 菜单信息缓存 */
    String SYS_MENU = "sys_menu";

    /** 登录用户 Token 缓存 */
    String LOGIN_TOKEN = "login_token";

    /** OSS 对象存储缓存 */
    String SYS_OSS = "sys_oss";

    /** 字典缓存 */
    String SYS_DICT = "sys_dict";

    /** 字典类型缓存 */
    String SYS_DICT_TYPE = "sys_dict_type";

    /** 字典数据缓存 */
    String SYS_DICT_DATA = "sys_dict_data";
}
