package com.scaffold.system.controller;

import com.scaffold.common.core.model.Result;
import com.scaffold.common.web.core.BaseController;
import com.scaffold.system.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口。
 *
 * <p>提供登录、退出登录、登录状态查询等基础能力。</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController extends BaseController {

    private final AuthService authService;

    /**
     * 用户登录。
     *
     * @param request 登录参数
     * @return 登录成功后的 Token
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return Result.ok(token);
    }

    /**
     * 用户退出登录。
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok();
    }

    /**
     * 查询当前 Token 是否有效。
     */
    @GetMapping("/is-login")
    public Result<Boolean> isLogin() {
        return Result.ok(authService.isLogin());
    }

    /** 登录请求参数。 */
    @Data
    public static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }
}
