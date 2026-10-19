package com.scaffold.api.system.service;

import com.scaffold.api.system.domain.vo.DubboLoginUser;

/**
 * 认证服务接口（Dubbo RPC）。
 *
 * <p>提供给其他微服务调用的身份认证接口。</p>
 *
 *  @author 8800
 */
public interface DubboAuthService {

    /**
     * 用户登录（远程调用）。
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录用户信息（包含 Token），失败返回 null
     */
    DubboLoginUser login(String username, String password);

    /**
     * 获取当前登录用户信息（无参方式）。
     *
     * <p>通过 Dubbo RpcContext 自动获取当前请求的 Token，适用于同进程内的服务调用。</p>
     *
     * @return 登录用户信息，不存在返回 null
     */
    DubboLoginUser getLoginUser();

    /**
     * 通过 Token 查询登录用户信息。
     *
     * @param token 认证 Token
     * @return 登录用户信息，不存在返回 null
     */
    DubboLoginUser getLoginUserByToken(String token);

    /**
     * 验证 Token 是否有效。
     *
     * @param token 认证 Token
     * @return true 有效，false 无效
     */
    boolean validateToken(String token);

    /**
     * 用户退出登录（无参方式）。
     */
    void logout();

    /**
     * 用户退出登录。
     *
     * @param token 认证 Token
     */
    void logoutByToken(String token);
}
