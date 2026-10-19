package com.scaffold.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scaffold.system.domain.entity.userrole.SysDept;
import com.scaffold.system.domain.vo.dept.SysDeptVo;

import java.util.List;

/**
 * 部门管理 Service 接口
 *
 * @author scaffold
 */
public interface DeptService extends IService<SysDept> {

    /**
     * 查询所有部门（树形结构）。
     */
    List<SysDeptVo> selectDeptTree();

    /**
     * 根据 ID 查询部门详情。
     */
    SysDeptVo selectDeptById(Long deptId);

    /**
     * 查询指定部门的子部门列表。
     */
    List<SysDept> selectChildrenByDeptId(Long deptId);

    /**
     * 查询部门列表。
     */
    List<SysDeptVo> selectDeptList(SysDept dept);

    /**
     * 新增部门。
     */
    int insertDept(SysDept dept);

    /**
     * 修改部门。
     */
    int updateDept(SysDept dept);

    /**
     * 删除部门。
     */
    int deleteDeptById(Long deptId);

    /**
     * 判断部门是否还有子部门。
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 判断部门下是否有用户。
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一。
     */
    boolean checkDeptNameUnique(SysDept dept);
}
