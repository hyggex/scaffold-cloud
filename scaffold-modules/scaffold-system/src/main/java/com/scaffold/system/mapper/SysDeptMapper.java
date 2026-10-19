package com.scaffold.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scaffold.system.domain.entity.userrole.SysDept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 部门 Mapper
 *
 *  @author 8800
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 根据部门 ID 查询部门名称。
     */
    @Select("SELECT dept_name FROM sys_dept WHERE dept_id = #{deptId} AND del_flag = '0'")
    String selectDeptNameById(@Param("deptId") Long deptId);

    /**
     * 根据用户 ID 查询用户的部门信息。
     */
    @Select("""
        SELECT d.* FROM sys_dept d
        INNER JOIN sys_user u ON u.dept_id = d.dept_id
        WHERE u.user_id = #{userId} AND d.del_flag = '0'
        """)
    SysDept selectDeptByUserId(@Param("userId") Long userId);

    /**
     * 查询所有正常状态的部门列表。
     */
    @Select("SELECT * FROM sys_dept WHERE del_flag = '0' ORDER BY ancestors, order_num")
    java.util.List<SysDept> selectDeptList();
}
