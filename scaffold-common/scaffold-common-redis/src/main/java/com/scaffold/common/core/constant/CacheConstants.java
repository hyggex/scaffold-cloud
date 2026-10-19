package com.scaffold.common.core.constant;

/**
 * 缓存 Key 常量。
 */
public interface CacheConstants {

    /** 在线用户 Token Redis Key */
    String ONLINE_TOKEN_KEY = "online_tokens:";

    /** 系统参数缓存 Redis Key */
    String SYS_CONFIG_KEY = "sys_config:";

    /** 字典缓存 Redis Key */
    String SYS_DICT_KEY = "sys_dict:";

    /** 密码错误次数 Redis Key */
    String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
}
