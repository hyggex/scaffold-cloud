package com.scaffold.system.service;

/**
 * 认证业务接口。
 */
public interface AuthService {

    /**
     * 用户登录。
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功后的 Token
     */
    String login(String username, String password);

    /**
     * 用户退出登录。
     */
    void logout();

    /**
     * 查询当前会话是否已登录。
     *
     * @return 是否已登录
     */
    boolean isLogin();
}
