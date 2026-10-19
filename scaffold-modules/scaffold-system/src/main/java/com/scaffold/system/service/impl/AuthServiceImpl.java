package com.scaffold.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scaffold.common.core.constant.SystemConstants;
import com.scaffold.common.core.exception.ServiceException;
import com.scaffold.common.security.util.LoginHelper;
import com.scaffold.common.security.util.LoginUser;
import com.scaffold.system.domain.entity.MenuEntity;
import com.scaffold.system.domain.entity.RoleEntity;
import com.scaffold.system.domain.entity.UserEntity;
import com.scaffold.system.domain.entity.userrole.SysDept;
import com.scaffold.system.mapper.SysDeptMapper;
import com.scaffold.system.mapper.SysRoleMapper;
import com.scaffold.system.mapper.UserMapper;
import com.scaffold.system.service.AuthService;
import com.scaffold.system.service.MenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * 认证业务实现。
 *
 * <p>支持用户名密码登录，登录成功后构建完整的 LoginUser 信息
 *（含用户基本信息、所属部门、角色列表、权限标识），并存入 Sa-Token Session。</p>
 *
 *  @author 8800
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysDeptMapper deptMapper;
    private final MenuService menuService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String username, String password) {
        // 1. 查询用户
        UserEntity user = userMapper.selectOne(
            new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username)
                .eq(UserEntity::getDelFlag, SystemConstants.NORMAL)
        );
        if (ObjectUtil.isNull(user)) {
            throw new ServiceException("用户不存在");
        }

        // 2. 校验密码（BCrypt 比对）
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ServiceException("用户名或密码错误");
        }

        // 3. 校验账号状态
        if (!SystemConstants.NORMAL.equals(user.getStatus())) {
            throw new ServiceException("账号已停用，请联系管理员");
        }

        // 4. 构建登录用户完整信息
        LoginUser loginUser = buildLoginUser(user);

        // 5. Sa-Token 登录（可传入 loginType 区分不同用户体系）
        StpUtil.login(user.getUserId());
        StpUtil.getSession().set(LoginHelper.LOGIN_USER_KEY, loginUser);

        // 6. 更新登录信息
        UserEntity updateUser = new UserEntity();
        updateUser.setUserId(user.getUserId());
        updateUser.setLoginIp(getIp());
        updateUser.setLoginDate(LocalDateTime.now());
        userMapper.updateById(updateUser);

        log.info("用户 {} 登录成功，IP: {}", username, getIp());
        return StpUtil.getTokenValue();
    }

    @Override
    public void logout() {
        Long userId = LoginHelper.getUserId();
        if (userId != null) {
            log.info("用户 {} 退出登录", LoginHelper.getUsername());
        }
        StpUtil.logout();
    }

    @Override
    public boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 构建完整的登录用户信息。
     *
     * @param user 用户实体
     * @return 登录用户信息
     */
    private LoginUser buildLoginUser(UserEntity user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setStatus(user.getStatus());
        loginUser.setUserType(user.getUserType());

        // 部门信息
        Long deptId = user.getDeptId();
        if (deptId != null && deptId > 0) {
            loginUser.setDeptId(deptId);
            SysDept dept = deptMapper.selectDeptByUserId(user.getUserId());
            if (dept != null) {
                loginUser.setDeptName(dept.getDeptName());
            }
        }

        // 角色信息
        Set<Long> roleIds = roleMapper.selectRoleIdsByUserId(user.getUserId());
        Set<String> roleKeys = roleMapper.selectRoleKeysByUserId(user.getUserId());
        Set<String> roleNames = roleMapper.selectRoleNamesByUserId(user.getUserId());
        loginUser.setRoleIds(roleIds);
        loginUser.setRoleKeys(roleKeys);
        loginUser.setRoleNames(roleNames);

        // 权限信息（从菜单权限标识中提取）
        Set<String> permissions;
        if (loginUser.isSuperAdmin()) {
            // 超级管理员拥有所有权限标识
            permissions = menuService.selectMenuPermsByUserId(user.getUserId());
            // 补充通配符表示全部权限
            permissions.add("*:*:*");
        } else {
            permissions = menuService.selectMenuPermsByUserId(user.getUserId());
        }
        loginUser.setPermissions(permissions);

        // 登录 IP
        loginUser.setLoginIp(getIp());
        loginUser.setLoginDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return loginUser;
    }

    private String getIp() {
        try {
            Object ipObj = StpUtil.getExtra("loginIp");
            return ipObj != null ? ipObj.toString() : "127.0.0.1";
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }
}
