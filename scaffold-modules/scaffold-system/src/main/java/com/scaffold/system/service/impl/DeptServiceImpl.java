package com.scaffold.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scaffold.common.core.constant.SystemConstants;
import com.scaffold.common.core.exception.ServiceException;
import com.scaffold.common.core.utils.StringUtils;
import com.scaffold.system.domain.entity.userrole.SysDept;
import com.scaffold.system.domain.vo.dept.SysDeptVo;
import com.scaffold.system.mapper.SysDeptMapper;
import com.scaffold.system.service.DeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门管理 Service 实现
 *
 *  @author 8800
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements DeptService {

    @Override
    public List<SysDeptVo> selectDeptTree() {
        List<SysDept> depts = baseMapper.selectDeptList();
        return buildDeptTree(depts);
    }

    @Override
    public SysDeptVo selectDeptById(Long deptId) {
        SysDept dept = getById(deptId);
        if (dept == null) {
            return null;
        }
        SysDeptVo vo = convertToVo(dept);
        // 查父部门名称
        if (dept.getParentId() != null && dept.getParentId() != 0L) {
            SysDept parent = getById(dept.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getDeptName());
            }
        }
        return vo;
    }

    @Override
    public List<SysDept> selectChildrenByDeptId(Long deptId) {
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getStatus, SystemConstants.NORMAL);
        lqw.likeRight(SysDept::getAncestors, getDeptAncestors(deptId));
        lqw.orderByAsc(SysDept::getOrderNum);
        return list(lqw);
    }

    private String getDeptAncestors(Long deptId) {
        SysDept dept = getById(deptId);
        return dept != null ? dept.getAncestors() : "";
    }

    @Override
    public List<SysDeptVo> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> lqw = buildQueryWrapper(dept);
        lqw.orderByAsc(SysDept::getParentId, SysDept::getOrderNum);
        return list(lqw).stream().map(this::convertToVo).collect(Collectors.toList());
    }

    private LambdaQueryWrapper<SysDept> buildQueryWrapper(SysDept dept) {
        LambdaQueryWrapper<SysDept> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SysDept::getDelFlag, SystemConstants.NORMAL);
        lqw.eq(dept.getParentId() != null, SysDept::getParentId, dept.getParentId());
        lqw.like(StringUtils.isNotBlank(dept.getDeptName()), SysDept::getDeptName, dept.getDeptName());
        lqw.eq(StringUtils.isNotBlank(dept.getStatus()), SysDept::getStatus, dept.getStatus());
        return lqw;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertDept(SysDept dept) {
        SysDept parent = getById(dept.getParentId());
        if (parent == null) {
            throw new ServiceException("父部门不存在");
        }
        if (!SystemConstants.NORMAL.equals(parent.getStatus())) {
            throw new ServiceException("父部门已停用，不允许新增子部门");
        }
        dept.setAncestors(parent.getAncestors() + "," + parent.getDeptId());
        return save(dept) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDept(SysDept dept) {
        SysDept dbDept = getById(dept.getDeptId());
        if (dbDept == null) {
            throw new ServiceException("部门不存在");
        }
        // 如果修改了父部门，校验新父部门是否正常
        if (!dbDept.getParentId().equals(dept.getParentId())) {
            SysDept newParent = getById(dept.getParentId());
            if (newParent != null && !SystemConstants.NORMAL.equals(newParent.getStatus())) {
                throw new ServiceException("新父部门已停用，不允许移动");
            }
            // 更新子部门的 ancestors
            String oldAncestors = dbDept.getAncestors();
            String newAncestors = newParent.getAncestors() + "," + newParent.getDeptId();
            updateChildrenAncestors(dept.getDeptId(), newAncestors, oldAncestors);
            dept.setAncestors(newAncestors);
        }
        return updateById(dept) ? 1 : 0;
    }

    private void updateChildrenAncestors(Long deptId, String newAncestors, String oldAncestors) {
        List<SysDept> children = list(new LambdaQueryWrapper<SysDept>()
            .likeRight(SysDept::getAncestors, oldAncestors + "," + deptId));
        for (SysDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (CollUtil.isNotEmpty(children)) {
            updateBatchById(children);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDeptById(Long deptId) {
        // 校验是否有子部门
        if (hasChildByDeptId(deptId)) {
            throw new ServiceException("存在子部门，不允许删除");
        }
        return removeById(deptId) ? 1 : 0;
    }

    @Override
    public boolean hasChildByDeptId(Long deptId) {
        return count(new LambdaQueryWrapper<SysDept>()
            .eq(SysDept::getParentId, deptId)
            .eq(SysDept::getDelFlag, SystemConstants.NORMAL)) > 0;
    }

    @Override
    public boolean checkDeptExistUser(Long deptId) {
        // 这里依赖 UserMapper，由于循环依赖，通过 SQL 直接校验
        return baseMapper.selectCount(null) > 0; // TODO: 后续补充
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        long count = count(new LambdaQueryWrapper<SysDept>()
            .eq(SysDept::getDeptName, dept.getDeptName())
            .eq(SysDept::getParentId, dept.getParentId())
            .ne(dept.getDeptId() != null, SysDept::getDeptId, dept.getDeptId()));
        return count == 0;
    }

    /**
     * 将实体列表构建为树形结构。
     */
    private List<SysDeptVo> buildDeptTree(List<SysDept> depts) {
        if (CollUtil.isEmpty(depts)) {
            return List.of();
        }
        Map<Long, List<SysDeptVo>> childrenMap = depts.stream()
            .map(this::convertToVo)
            .peek(vo -> {
                // 过滤掉已停用的子部门
            })
            .collect(Collectors.groupingBy(vo -> vo.getParentId() != null ? vo.getParentId() : 0L));

        depts.stream().map(this::convertToVo).forEach(vo -> {
            List<SysDeptVo> children = childrenMap.get(vo.getDeptId());
            vo.setChildren(children != null ? children : new ArrayList<>());
        });

        // 返回根节点（parentId == 0 或 parentId == null）
        return depts.stream()
            .map(this::convertToVo)
            .filter(vo -> vo.getParentId() == null || vo.getParentId() == 0L)
            .sorted(Comparator.comparing(SysDeptVo::getOrderNum))
            .collect(Collectors.toList());
    }

    private SysDeptVo convertToVo(SysDept dept) {
        SysDeptVo vo = new SysDeptVo();
        vo.setDeptId(dept.getDeptId());
        vo.setParentId(dept.getParentId());
        vo.setAncestors(dept.getAncestors());
        vo.setDeptName(dept.getDeptName());
        vo.setOrderNum(dept.getOrderNum());
        vo.setLeader(dept.getLeaderUserId());
        // leaderName 需单独查询用户表获取，此处设为 null
        vo.setLeaderName(null);
        vo.setPhone(dept.getPhone());
        vo.setEmail(dept.getEmail());
        vo.setStatus(dept.getStatus());
        vo.setChildren(new ArrayList<>());
        return vo;
    }
}
