package com.scaffold.common.core.constant;

/**
 * 缓存名称常量。
 *
 * <p>Key 格式为 cacheNames#ttl#maxIdleTime#maxSize#local：
 * <ul>
 *   <li>ttl：过期时间，过期为0或不设置则永不过期，默认为0</li>
 *   <li>maxIdleTime：最大空闲时间，超过LRU算法淘汰，不设置为0不淘汰，默认为0</li>
 *   <li>maxSize：缓存最大条数，超过LRU算法淘汰，不设置为0不淘汰，默认为0</li>
 *   <li>local：默认开启本地二级缓存为1，关闭本地二级缓存为0</li>
 * </ul>
 *
 * <p>示例：test#60s、test#0#60s、test#0#1m#1000、test#1h#0#500、test#1h#0#500#0
 */
public interface CacheNames {

    /** 示例缓存 */
    String DEMO_CACHE = "demo:cache#60s#10m#20";

    /** 系统参数 */
    String SYS_CONFIG = "sys_config";

    /** 字典数据 */
    String SYS_DICT = "sys_dict";

    /** 字典类型 */
    String SYS_DICT_TYPE = "sys_dict_type";

    /** 租户信息 */
    String SYS_TENANT = GlobalConstants.GLOBAL_REDIS_KEY + "sys_tenant#30d";

    /** 客户端信息 */
    String SYS_CLIENT = GlobalConstants.GLOBAL_REDIS_KEY + "sys_client#30d";

    /** 用户账号（用于校验重复） */
    String SYS_USER_NAME = "sys_user_name#30d";

    /** 用户昵称 */
    String SYS_NICKNAME = "sys_nickname#30d";

    /** 部门信息 */
    String SYS_DEPT = "sys_dept#30d";

    /** OSS 配置 */
    String SYS_OSS = "sys_oss#30d";

    /** OSS 配置（全局共享） */
    String SYS_OSS_CONFIG = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss_config";

    /** 在线用户 */
    String ONLINE_TOKEN = "online_tokens";
}
