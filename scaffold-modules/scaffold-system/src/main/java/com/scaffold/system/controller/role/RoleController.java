package com.scaffold.system.controller.role;

import com.scaffold.common.core.model.Result;
import com.scaffold.common.log.annotation.Log;
import com.scaffold.common.log.enums.BusinessType;
import com.scaffold.common.security.util.LoginHelper;
import com.scaffold.common.web.core.BaseController;
import com.scaffold.system.domain.entity.RoleEntity;
import com.scaffold.system.domain.vo.RoleVo;
import com.scaffold.system.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 *
 * @author scaffold
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/role")
public class RoleController extends BaseController {

    private final RoleService roleService;

    /**
     * 查询角色列表。
     */
    @GetMapping("/list")
    public Result<List<RoleVo>> list() {
        return Result.ok(roleService.selectRoleAll());
    }

    /**
     * 查询当前用户的角色列表。
     */
    @GetMapping("/listByCurrentUser")
    public Result<List<RoleVo>> listByCurrentUser() {
        Long userId = LoginHelper.getUserId();
        return Result.ok(roleService.selectRoleListByUserId(userId));
    }

    /**
     * 根据角色 ID 查询详情。
     */
    @GetMapping("/{roleId}")
    public Result<RoleVo> getInfo(@PathVariable Long roleId) {
        return Result.ok(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色。
     */
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody RoleEntity role,
                            @RequestParam List<Long> menuIds) {
        roleService.insertRole(role, menuIds);
        return Result.ok();
    }

    /**
     * 修改角色。
     */
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody RoleEntity role,
                             @RequestParam List<Long> menuIds) {
        roleService.updateRole(role, menuIds);
        return Result.ok();
    }

    /**
     * 修改角色状态。
     */
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public Result<Void> changeStatus(@RequestParam Long roleId,
                                     @RequestParam String status) {
        roleService.updateRoleStatus(roleId, status);
        return Result.ok();
    }

    /**
     * 删除角色。
     */
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@PathVariable List<Long> roleIds) {
        roleService.deleteRoleByIds(roleIds);
        return Result.ok();
    }
}
