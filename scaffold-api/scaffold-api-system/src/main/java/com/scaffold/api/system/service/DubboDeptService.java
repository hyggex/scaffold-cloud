package com.scaffold.api.system.service;

import com.scaffold.api.system.domain.vo.DubboDeptVo;

import java.util.List;

/**
 * 部门服务接口（Dubbo RPC）。
 *
 * <p>提供给其他微服务调用的部门查询接口。</p>
 *
 *  @author 8800
 */
public interface DubboDeptService {

    /**
     * 根据部门ID查询部门信息。
     *
     * @param deptId 部门ID
     * @return 部门信息，不存在返回 null
     */
    DubboDeptVo getDeptById(Long deptId);

    /**
     * 查询所有部门（树形结构）。
     *
     * @return 部门树
     */
    List<DubboDeptVo> getDeptTree();

    /**
     * 查询指定部门的子部门ID列表。
     *
     * @param deptId 部门ID
     * @return 子部门ID列表
     */
    List<Long> getChildDeptIds(Long deptId);

    /**
     * 查询指定部门的所有祖先部门ID列表。
     *
     * @param deptId 部门ID
     * @return 祖先部门ID列表
     */
    List<Long> getAncestorDeptIds(Long deptId);

    /**
     * 根据部门ID列表批量查询部门信息。
     *
     * @param deptIds 部门ID集合
     * @return 部门信息列表
     */
    List<DubboDeptVo> getDeptsByIds(List<Long> deptIds);

    /**
     * 判断部门是否还有子部门。
     *
     * @param deptId 部门ID
     * @return true 有子部门，false 没有
     */
    boolean hasChildDept(Long deptId);

    /**
     * 判断部门下是否有用户。
     *
     * @param deptId 部门ID
     * @return true 有用户，false 没有
     */
    boolean hasUsers(Long deptId);
}
