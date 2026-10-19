package com.scaffold.system.controller;

import com.scaffold.common.core.model.Result;
import com.scaffold.common.web.core.BaseController;
import com.scaffold.system.domain.entity.UserEntity;
import com.scaffold.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户管理接口。
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    /**
     * 根据用户 ID 查询用户信息。
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<UserEntity> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }
}
