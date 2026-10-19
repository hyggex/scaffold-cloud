-- =============================================================
-- scaffold 微服务脚手架 数据库初始化脚本
-- 数据库：MySQL 8.0
-- =============================================================

-- ----------------------------
-- 1. OSS 对象存储表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_oss` (
  `oss_id`          bigint(20)   NOT NULL                   COMMENT '对象存储主键',
  `file_name`       varchar(255) NOT NULL DEFAULT ''        COMMENT '文件名（对象键）',
  `original_name`   varchar(255) NOT NULL DEFAULT ''        COMMENT '原文件名',
  `file_suffix`     varchar(20)  NOT NULL DEFAULT ''        COMMENT '文件后缀名',
  `url`             varchar(500) NOT NULL                   COMMENT 'URL地址',
  `file_url`        varchar(500) NOT NULL DEFAULT ''        COMMENT '文件路径（无前缀）',
  `service`         varchar(20)  NOT NULL DEFAULT 'minio'  COMMENT '服务商',
  `ext1`            text                             DEFAULT NULL COMMENT '扩展字段（JSON）',
  `create_dept`     bigint(20)                       DEFAULT NULL COMMENT '创建部门',
  `create_by`       bigint(20)                       DEFAULT NULL COMMENT '创建人',
  `create_time`     datetime                          DEFAULT NULL COMMENT '创建时间',
  `update_by`       bigint(20)                       DEFAULT NULL COMMENT '更新人',
  `update_time`     datetime                          DEFAULT NULL COMMENT '更新时间',
  `del_flag`        char(1)       DEFAULT '0'            COMMENT '删除标志（0=存在,1=删除）',
  `remark`          varchar(500)                      DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`oss_id`),
  KEY `idx_file_name` (`file_name`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS对象存储表';

-- ----------------------------
-- 2. 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
  `user_id`      bigint(20)   NOT NULL                   COMMENT '用户ID',
  `dept_id`      bigint(20)   DEFAULT NULL               COMMENT '部门ID',
  `user_name`    varchar(30)  NOT NULL                   COMMENT '用户账号',
  `nick_name`    varchar(30)  NOT NULL                   COMMENT '用户昵称',
  `user_type`    varchar(2)   DEFAULT '00'              COMMENT '用户类型（00系统用户）',
  `email`        varchar(50)   DEFAULT ''                COMMENT '用户邮箱',
  `phone_number` varchar(11)   DEFAULT ''                COMMENT '手机号码',
  `sex`          char(1)      DEFAULT '0'               COMMENT '用户性别（0男 1女 2未知）',
  `avatar`       varchar(100)  DEFAULT ''                COMMENT '头像地址',
  `password`     varchar(100)  DEFAULT ''                COMMENT '密码',
  `status`       char(1)      DEFAULT '0'               COMMENT '帐号状态（0正常 1停用）',
  `login_ip`     varchar(128)  DEFAULT ''                COMMENT '最后登录IP',
  `login_date`   datetime     DEFAULT NULL              COMMENT '最后登录时间',
  `create_by`    bigint(20)   DEFAULT NULL              COMMENT '创建者',
  `create_time`  datetime     DEFAULT NULL              COMMENT '创建时间',
  `update_by`    bigint(20)   DEFAULT NULL              COMMENT '更新者',
  `update_time`  datetime     DEFAULT NULL              COMMENT '更新时间',
  `del_flag`     char(1)      DEFAULT '0'               COMMENT '删除标志（0=存在,1=删除）',
  `remark`       varchar(500)  DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_user_name` (`user_name`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- 3. 角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
  `role_id`        bigint(20)   NOT NULL                   COMMENT '角色ID',
  `role_name`      varchar(30)  NOT NULL                   COMMENT '角色名称',
  `role_key`       varchar(100) NOT NULL                   COMMENT '角色权限字符串',
  `role_sort`      int(4)      NOT NULL                   COMMENT '显示顺序',
  `data_scope`     char(1)     DEFAULT '1'               COMMENT '数据范围（1=全部, 2=本部门, 3=本部门及以下, 4=仅本人, 5=自定义）',
  `menu_check_strictly` tinyint(1) DEFAULT 1              COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT 1              COMMENT '部门树选择项是否关联显示',
  `status`         char(1)     NOT NULL                   COMMENT '角色状态（0正常 1停用）',
  `create_by`      bigint(20)   DEFAULT NULL              COMMENT '创建者',
  `create_time`    datetime     DEFAULT NULL              COMMENT '创建时间',
  `update_by`      bigint(20)   DEFAULT NULL              COMMENT '更新者',
  `update_time`    datetime     DEFAULT NULL              COMMENT '更新时间',
  `del_flag`      char(1)      DEFAULT '0'               COMMENT '删除标志（0=存在,1=删除）',
  `remark`         varchar(500)  DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `idx_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- 4. 菜单权限表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
  `menu_id`       bigint(20)   NOT NULL                   COMMENT '菜单ID',
  `menu_name`     varchar(30)  NOT NULL                   COMMENT '菜单名称',
  `parent_id`     bigint(20)   DEFAULT 0                  COMMENT '父菜单ID',
  `order_num`     int(4)      DEFAULT 0                  COMMENT '显示顺序',
  `path`          varchar(200)  DEFAULT ''                COMMENT '路由地址',
  `component`      varchar(255)  DEFAULT NULL              COMMENT '组件路径',
  `query`          varchar(255)  DEFAULT NULL              COMMENT '路由参数',
  `is_frame`       int(1)      DEFAULT 1                  COMMENT '是否为外链（0是 1否）',
  `is_cache`       int(1)      DEFAULT 0                  COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type`      char(1)     DEFAULT ''                COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible`        char(1)     DEFAULT '0'               COMMENT '菜单状态（0显示 1隐藏）',
  `status`         char(1)     DEFAULT '0'               COMMENT '菜单状态（0正常 1停用）',
  `perms`          varchar(100)  DEFAULT NULL              COMMENT '权限标识',
  `icon`           varchar(100)  DEFAULT '#'                COMMENT '菜单图标',
  `create_by`      bigint(20)   DEFAULT NULL              COMMENT '创建者',
  `create_time`    datetime     DEFAULT NULL              COMMENT '创建时间',
  `update_by`      bigint(20)   DEFAULT NULL              COMMENT '更新者',
  `update_time`    datetime     DEFAULT NULL              COMMENT '更新时间',
  `del_flag`      char(1)      DEFAULT '0'               COMMENT '删除标志（0=存在,1=删除）',
  `remark`         varchar(500)  DEFAULT ''                COMMENT '备注',
  PRIMARY KEY (`menu_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 5. 用户和角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
  `user_id`   bigint(20) NOT NULL COMMENT '用户ID',
  `role_id`   bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ----------------------------
-- 6. 角色和菜单关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
  `role_id`  bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id`  bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ----------------------------
-- 7. 部门表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dept` (
  `dept_id`       bigint(20)   NOT NULL                   COMMENT '部门id',
  `parent_id`     bigint(20)   DEFAULT 0                  COMMENT '父部门id',
  `ancestors`     varchar(50)   DEFAULT ''                COMMENT '祖级列表',
  `dept_name`     varchar(30)  NOT NULL                   COMMENT '部门名称',
  `order_num`     int(4)      DEFAULT 0                  COMMENT '显示顺序',
  `leader_user_id` bigint(20)  DEFAULT NULL              COMMENT '负责人',
  `phone`          varchar(11)   DEFAULT NULL              COMMENT '联系电话',
  `email`          varchar(50)   DEFAULT NULL              COMMENT '邮箱',
  `status`         char(1)     DEFAULT '0'               COMMENT '部门状态（0正常 1停用）',
  `del_flag`      char(1)      DEFAULT '0'               COMMENT '删除标志（0=存在,1=删除）',
  `create_by`      bigint(20)   DEFAULT NULL              COMMENT '创建者',
  `create_time`    datetime     DEFAULT NULL              COMMENT '创建时间',
  `update_by`      bigint(20)   DEFAULT NULL              COMMENT '更新者',
  `update_time`    datetime     DEFAULT NULL              COMMENT '更新时间',
  PRIMARY KEY (`dept_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 8. 字典类型表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_type` (
  `dict_id`     bigint(20)   NOT NULL                   COMMENT '字典主键',
  `dict_name`   varchar(100)  DEFAULT ''                COMMENT '字典名称',
  `dict_type`   varchar(100)  DEFAULT ''                COMMENT '字典类型',
  `status`       char(1)      DEFAULT '0'               COMMENT '状态（0正常 1停用）',
  `create_by`    bigint(20)   DEFAULT NULL              COMMENT '创建者',
  `create_time`  datetime     DEFAULT NULL              COMMENT '创建时间',
  `update_by`    bigint(20)   DEFAULT NULL              COMMENT '更新者',
  `update_time`  datetime     DEFAULT NULL              COMMENT '更新时间',
  `remark`       varchar(500)  DEFAULT NULL             COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 9. 字典数据表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
  `dict_code`   bigint(20)   NOT NULL                   COMMENT '字典编码',
  `dict_sort`    int(4)      DEFAULT 0                  COMMENT '字典排序',
  `dict_label`   varchar(100)  DEFAULT ''                COMMENT '字典标签',
  `dict_value`   varchar(100)  DEFAULT ''                COMMENT '字典键值',
  `dict_type`   varchar(100)  DEFAULT ''                COMMENT '字典类型',
  `css_class`    varchar(100)  DEFAULT NULL              COMMENT '样式属性',
  `list_class`   varchar(100)  DEFAULT NULL              COMMENT '表格回显样式',
  `is_default`   char(1)      DEFAULT 'N'               COMMENT '是否默认（Y是 N否）',
  `status`       char(1)      DEFAULT '0'               COMMENT '状态（0正常 1停用）',
  `create_by`    bigint(20)   DEFAULT NULL              COMMENT '创建者',
  `create_time`  datetime     DEFAULT NULL              COMMENT '创建时间',
  `update_by`    bigint(20)   DEFAULT NULL              COMMENT '更新者',
  `update_time`  datetime     DEFAULT NULL              COMMENT '更新时间',
  `remark`       varchar(500)  DEFAULT NULL              COMMENT '备注',
  PRIMARY KEY (`dict_code`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 10. 操作日志表（精简版）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_oper_log` (
  `oper_id`       bigint(20)   NOT NULL                   COMMENT '日志主键',
  `title`         varchar(50)   DEFAULT ''                COMMENT '模块标题',
  `business_type`  int(2)      DEFAULT 0                  COMMENT '业务类型（0=其它 1=新增 2=修改 3=删除）',
  `method`        varchar(100)  DEFAULT ''                COMMENT '方法名称',
  `request_method` varchar(10)  DEFAULT ''                COMMENT '请求方式',
  `operator_type`  int(1)      DEFAULT 0                  COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name`     varchar(50)   DEFAULT ''                COMMENT '操作人员',
  `dept_name`     varchar(50)   DEFAULT ''                COMMENT '部门名称',
  `oper_url`      varchar(255)  DEFAULT ''                COMMENT '请求URL',
  `oper_ip`       varchar(128)  DEFAULT ''                COMMENT '主机地址',
  `oper_location`  varchar(255)  DEFAULT ''                COMMENT '操作地点',
  `oper_param`    varchar(2000) DEFAULT ''                COMMENT '请求参数',
  `json_result`   varchar(2000) DEFAULT ''                COMMENT '返回参数',
  `status`        int(1)      DEFAULT 0                  COMMENT '操作状态（0正常 1异常）',
  `error_msg`     varchar(2000) DEFAULT ''                COMMENT '错误消息',
  `oper_time`     datetime     DEFAULT NULL              COMMENT '操作时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录';

-- ----------------------------
-- 11. 登录日志表（精简版）
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_logininfor` (
  `info_id`     bigint(20)   NOT NULL                   COMMENT '访问ID',
  `user_name`   varchar(50)   DEFAULT ''                COMMENT '用户账号',
  `ipaddr`      varchar(128)  DEFAULT ''                COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT ''                COMMENT '登录地点',
  `browser`      varchar(50)   DEFAULT ''                COMMENT '浏览器类型',
  `os`           varchar(50)   DEFAULT ''                COMMENT '操作系统',
  `status`       char(1)      DEFAULT '0'               COMMENT '登录状态（0成功 1失败）',
  `msg`          varchar(255)  DEFAULT ''                COMMENT '提示消息',
  `login_time`   datetime     DEFAULT NULL              COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问日志';

-- =============================================================
-- 预置数据
-- =============================================================

-- 预置管理员用户（密码: admin123，使用 Sa-Token MD5 加密）
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '管理员', '00', 'admin@scaffold.com', '15888888888', '0', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU1Eq4FMJG', '0', '127.0.0.1', NOW(), 1, NOW(), NULL, NOW(), '0', '管理员账号');

-- 预置角色（超管）
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', 1, NOW(), NULL, NOW(), '0', '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '1', 1, 1, '0', 1, NOW(), NULL, NOW(), '0', '普通角色');

-- 预置菜单
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (2, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (3, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (4, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (5, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (100, '用户新增', 2, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (101, '用户编辑', 2, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (102, '用户删除', 2, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (103, '角色新增', 3, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (104, '角色编辑', 3, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (105, '角色删除', 3, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (106, '菜单新增', 4, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (107, '菜单编辑', 4, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 1, NOW(), NULL, NOW(), '0', '');
INSERT INTO `sys_menu` VALUES (108, '菜单删除', 4, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 1, NOW(), NULL, NOW(), '0', '');

-- 预置部门
INSERT INTO `sys_dept` VALUES (103, 0, '0', 'Scaffold集团', 0, 1, '15888888888', 'admin@scaffold.com', '0', '0', 1, NOW(), NULL, NOW());

-- 预置用户角色关联
INSERT INTO `sys_user_role` VALUES (1, 1);

-- 预置角色菜单关联
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (1, 5);
INSERT INTO `sys_role_menu` VALUES (1, 100);
INSERT INTO `sys_role_menu` VALUES (1, 101);
INSERT INTO `sys_role_menu` VALUES (1, 102);
INSERT INTO `sys_role_menu` VALUES (1, 103);
INSERT INTO `sys_role_menu` VALUES (1, 104);
INSERT INTO `sys_role_menu` VALUES (1, 105);
INSERT INTO `sys_role_menu` VALUES (1, 106);
INSERT INTO `sys_role_menu` VALUES (1, 107);
INSERT INTO `sys_role_menu` VALUES (1, 108);

-- 预置 OSS 配置（MinIO）
INSERT INTO `sys_oss_config` VALUES (1, '000000', 'minio', 'admin', 'admin123', 'scaffold', 'uploads', '127.0.0.1:9000', '', 'N', '', '1', '0', '', 103, 1, NOW(), 1, NOW(), NULL);

-- 预置字典类型
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 1, NOW(), NULL, NOW(), '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 1, NOW(), NULL, NOW(), '菜单状态列表');

-- 预置字典数据
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 1, NOW(), NULL, NOW(), '男性');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 1, NOW(), NULL, NOW(), '女性');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 1, NOW(), NULL, NOW(), '未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 1, NOW(), NULL, NOW(), '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 1, NOW(), NULL, NOW(), '隐藏菜单');
