package com.scaffold.common.core.constant;

/**
 * 全局 Key 常量（业务无关的 Key）。
 */
public interface GlobalConstants {

    /** 全局 Redis Key 前缀（业务无关的 Key） */
    String GLOBAL_REDIS_KEY = "global:";

    /** 验证码 Redis Key */
    String CAPTCHA_CODE_KEY = GLOBAL_REDIS_KEY + "captcha_codes:";

    /** 防重复提交 Redis Key */
    String REPEAT_SUBMIT_KEY = GLOBAL_REDIS_KEY + "repeat_submit:";

    /** 限流 Redis Key */
    String RATE_LIMIT_KEY = GLOBAL_REDIS_KEY + "rate_limit:";

    /** 第三方登录授权码 Redis Key */
    String SOCIAL_AUTH_CODE_KEY = GLOBAL_REDIS_KEY + "social_auth_codes:";
}
