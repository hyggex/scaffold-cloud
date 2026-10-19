package com.scaffold.system.controller.dept;

import com.scaffold.common.core.model.Result;
import com.scaffold.common.log.annotation.Log;
import com.scaffold.common.log.enums.BusinessType;
import com.scaffold.common.web.core.BaseController;
import com.scaffold.system.domain.entity.userrole.SysDept;
import com.scaffold.system.domain.vo.dept.SysDeptVo;
import com.scaffold.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理 Controller
 *
 * @author scaffold
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dept")
public class DeptController extends BaseController {

    private final DeptService deptService;

    /**
     * 查询部门树（前端下拉树）。
     */
    @GetMapping("/tree")
    public Result<List<SysDeptVo>> tree() {
        return Result.ok(deptService.selectDeptTree());
    }

    /**
     * 查询部门列表。
     */
    @GetMapping("/list")
    public Result<List<SysDeptVo>> list(SysDept dept) {
        return Result.ok(deptService.selectDeptList(dept));
    }

    /**
     * 根据 ID 查询部门详情。
     */
    @GetMapping("/{deptId}")
    public Result<SysDeptVo> getInfo(@PathVariable Long deptId) {
        return Result.ok(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门。
     */
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysDept dept) {
        deptService.insertDept(dept);
        return Result.ok();
    }

    /**
     * 修改部门。
     */
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.ok();
    }

    /**
     * 删除部门。
     */
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@PathVariable Long deptId) {
        deptService.deleteDeptById(deptId);
        return Result.ok();
    }
}
