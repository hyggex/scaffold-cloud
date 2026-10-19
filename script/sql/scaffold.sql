-- =============================================================
-- scaffold 微服务脚手架 数据库初始化脚本
-- 数据库：MySQL 8.0
-- 说明：包含完整的表结构（带字段注释）和预置数据
-- =============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id`        bigint(20)      NOT NULL                          COMMENT '用户ID（主键）',
  `dept_id`        bigint(20)      DEFAULT NULL                      COMMENT '所属部门ID',
  `user_name`      varchar(30)     NOT NULL                          COMMENT '用户账号（唯一）',
  `nick_name`      varchar(30)     NOT NULL                          COMMENT '用户昵称',
  `user_type`      varchar(2)      DEFAULT '00'                      COMMENT '用户类型（00=系统用户）',
  `email`          varchar(50)     DEFAULT ''                        COMMENT '用户邮箱',
  `phone_number`   varchar(11)     DEFAULT ''                        COMMENT '手机号码',
  `sex`            char(1)         DEFAULT '0'                      COMMENT '用户性别（0=男 1=女 2=未知）',
  `avatar`         varchar(100)    DEFAULT ''                        COMMENT '头像URL',
  `password`       varchar(100)    DEFAULT ''                        COMMENT '密码（BCrypt加密）',
  `status`         char(1)         DEFAULT '0'                      COMMENT '账号状态（0=正常 1=停用）',
  `login_ip`       varchar(128)    DEFAULT ''                        COMMENT '最后登录IP',
  `login_date`     datetime        DEFAULT NULL                     COMMENT '最后登录时间',
  `create_dept`    bigint(20)      DEFAULT NULL                     COMMENT '创建部门',
  `create_by`      bigint(20)      DEFAULT NULL                     COMMENT '创建者用户ID',
  `create_time`    datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`      bigint(20)      DEFAULT NULL                     COMMENT '更新者用户ID',
  `update_time`    datetime        DEFAULT NULL                     COMMENT '更新时间',
  `del_flag`       char(1)         DEFAULT '0'                      COMMENT '删除标志（0=存在 1=删除）',
  `remark`         varchar(500)    DEFAULT NULL                     COMMENT '备注',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_user_name` (`user_name`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- 2. 角色信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id`          bigint(20)      NOT NULL                          COMMENT '角色ID（主键）',
  `role_name`        varchar(30)     NOT NULL                          COMMENT '角色名称',
  `role_key`         varchar(100)    NOT NULL                          COMMENT '角色权限字符串（唯一）',
  `role_sort`        int(4)          NOT NULL                          COMMENT '显示顺序（数字越小越靠前）',
  `data_scope`       char(1)         DEFAULT '1'                       COMMENT '数据范围（1=全部数据 2=本部门数据 3=本部门及以下 4=仅本人数据 5=自定义）',
  `menu_check_strictly` tinyint(1)   DEFAULT 1                         COMMENT '菜单树选择项是否关联控制（0=否 1=是）',
  `dept_check_strictly` tinyint(1)   DEFAULT 1                         COMMENT '部门树选择项是否关联控制（0=否 1=是）',
  `status`           char(1)         NOT NULL                          COMMENT '角色状态（0=正常 1=停用）',
  `create_by`        bigint(20)      DEFAULT NULL                      COMMENT '创建者用户ID',
  `create_time`      datetime        DEFAULT NULL                      COMMENT '���建时间',
  `update_by`        bigint(20)      DEFAULT NULL                      COMMENT '更新者用户ID',
  `update_time`      datetime        DEFAULT NULL                      COMMENT '更新时间',
  `del_flag`         char(1)         DEFAULT '0'                       COMMENT '删除标志（0=存在 1=删除）',
  `remark`           varchar(500)    DEFAULT NULL                      COMMENT '备注',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

-- ----------------------------
-- 3. 菜单权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id`       bigint(20)      NOT NULL                          COMMENT '菜单ID（主键）',
  `menu_name`     varchar(30)     NOT NULL                          COMMENT '菜单名称',
  `parent_id`     bigint(20)      DEFAULT 0                        COMMENT '父菜单ID（0=顶级菜单）',
  `order_num`     int(4)          DEFAULT 0                        COMMENT '显示顺序',
  `path`          varchar(200)    DEFAULT ''                        COMMENT '路由地址（Path）',
  `component`     varchar(255)    DEFAULT NULL                      COMMENT '组件路径',
  `query`         varchar(255)   DEFAULT NULL                      COMMENT '路由参数（?key=value）',
  `is_frame`      int(1)          DEFAULT 1                         COMMENT '是否为外链（0=是 1=否）',
  `is_cache`      int(1)          DEFAULT 0                         COMMENT '是否缓存（0=缓存 1=不缓存）',
  `menu_type`     char(1)         DEFAULT ''                        COMMENT '菜单类型（M=目录 C=菜单 F=按钮）',
  `visible`       char(1)         DEFAULT '0'                       COMMENT '菜单状态（0=显示 1=隐藏）',
  `status`        char(1)         DEFAULT '0'                       COMMENT '菜单状态（0=正常 1=停用）',
  `perms`         varchar(100)    DEFAULT NULL                      COMMENT '权限标识（如 system:user:list）',
  `icon`          varchar(100)    DEFAULT '#'                       COMMENT '菜单图标',
  `create_by`     bigint(20)      DEFAULT NULL                      COMMENT '创建者用户ID',
  `create_time`   datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`     bigint(20)      DEFAULT NULL                      COMMENT '更新者用户ID',
  `update_time`   datetime        DEFAULT NULL                     COMMENT '更新时间',
  `del_flag`      char(1)         DEFAULT '0'                       COMMENT '删除标志（0=存在 1=删除）',
  `remark`        varchar(500)    DEFAULT ''                        COMMENT '备注',
  PRIMARY KEY (`menu_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- 4. 用户和角色关联表（多对多）
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id`   bigint(20) NOT NULL                          COMMENT '用户ID',
  `role_id`   bigint(20) NOT NULL                          COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ----------------------------
-- 5. 角色和菜单关联表（多对多）
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id`  bigint(20) NOT NULL                          COMMENT '角色ID',
  `menu_id`  bigint(20) NOT NULL                          COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ----------------------------
-- 6. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id`       bigint(20)      NOT NULL                          COMMENT '部门ID（主键）',
  `parent_id`     bigint(20)      DEFAULT 0                        COMMENT '父部门ID（0=顶级部门）',
  `ancestors`     varchar(50)     DEFAULT ''                        COMMENT '祖级列表（格式：0,100,101）',
  `dept_name`     varchar(30)     NOT NULL                          COMMENT '部门名称',
  `order_num`     int(4)          DEFAULT 0                        COMMENT '显示顺序',
  `leader_user_id` bigint(20)     DEFAULT NULL                     COMMENT '部门负责人用户ID',
  `phone`         varchar(11)     DEFAULT NULL                     COMMENT '联系电话',
  `email`         varchar(50)     DEFAULT NULL                      COMMENT '邮箱',
  `status`        char(1)         DEFAULT '0'                       COMMENT '部门状态（0=正常 1=停用）',
  `del_flag`      char(1)         DEFAULT '0'                       COMMENT '删除标志（0=存在 1=删除）',
  `create_by`     bigint(20)      DEFAULT NULL                     COMMENT '创建者用户ID',
  `create_time`   datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`     bigint(20)      DEFAULT NULL                     COMMENT '更新者用户ID',
  `update_time`   datetime        DEFAULT NULL                     COMMENT '更新时间',
  PRIMARY KEY (`dept_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 7. OSS 对象存储配置表
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config` (
  `oss_id`        bigint(20)      NOT NULL                          COMMENT 'OSS主键ID',
  `tenant_id`     varchar(20)     DEFAULT '000000'                  COMMENT '租户编号',
  `provider`      varchar(20)     DEFAULT 'minio'                   COMMENT '存储服务商（minio/aliyun/tencent/qiniu）',
  `access_key`    varchar(100)    DEFAULT ''                        COMMENT '访问密钥AK',
  `secret_key`    varchar(100)    DEFAULT ''                        COMMENT '访问密钥SK',
  `bucket_name`   varchar(100)    DEFAULT ''                        COMMENT '存储桶名称',
  `prefix`        varchar(100)    DEFAULT ''                        COMMENT '存储路径前缀',
  `endpoint`      varchar(500)    DEFAULT ''                        COMMENT '服务端点',
  `domain`        varchar(500)    DEFAULT ''                        COMMENT '自定义域名',
  `is_https`      char(1)         DEFAULT 'N'                       COMMENT '是否HTTPS（Y=是 N=否）',
  `access_policy` varchar(10)    DEFAULT '1'                       COMMENT '访问策略（0=private 1=public 2=custom）',
  `status`        char(1)         DEFAULT '1'                       COMMENT '状态（0=正常 1=停用）',
  `remark`        varchar(500)    DEFAULT NULL                      COMMENT '备注',
  `create_dept`   bigint(20)      DEFAULT NULL                     COMMENT '创建部门',
  `create_by`     bigint(20)      DEFAULT NULL                     COMMENT '创建者',
  `create_time`   datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`     bigint(20)      DEFAULT NULL                     COMMENT '更新者',
  `update_time`   datetime        DEFAULT NULL                     COMMENT '更新时间',
  PRIMARY KEY (`oss_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS对象存储配置表';

-- ----------------------------
-- 8. OSS 对象存储文件记录表
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss` (
  `oss_id`        bigint(20)      NOT NULL                          COMMENT '对象存储主键',
  `file_name`     varchar(255)    NOT NULL DEFAULT ''               COMMENT '文件名（对象键）',
  `original_name` varchar(255)    NOT NULL DEFAULT ''               COMMENT '原文件名',
  `file_suffix`   varchar(20)     NOT NULL DEFAULT ''               COMMENT '文件后缀名',
  `url`           varchar(500)    NOT NULL                          COMMENT '访问URL',
  `file_url`      varchar(500)    NOT NULL DEFAULT ''               COMMENT '文件路径（不含域名）',
  `service`       varchar(20)     NOT NULL DEFAULT 'minio'         COMMENT '服务商',
  `ext1`          text            DEFAULT NULL                      COMMENT '扩展字段1（JSON格式）',
  `ext2`          text            DEFAULT NULL                      COMMENT '扩展字段2',
  `create_dept`   bigint(20)      DEFAULT NULL                     COMMENT '创建部门',
  `create_by`     bigint(20)      DEFAULT NULL                     COMMENT '创建者',
  `create_time`   datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`     bigint(20)      DEFAULT NULL                     COMMENT '更新者',
  `update_time`   datetime        DEFAULT NULL                     COMMENT '更新时间',
  `del_flag`      char(1)         DEFAULT '0'                       COMMENT '删除标志（0=存在 1=删除）',
  `remark`        varchar(500)    DEFAULT NULL                      COMMENT '备注',
  PRIMARY KEY (`oss_id`),
  KEY `idx_file_name` (`file_name`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS对象存储文件记录表';

-- ----------------------------
-- 9. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id`     bigint(20)      NOT NULL                          COMMENT '字典主键',
  `dict_name`   varchar(100)   DEFAULT ''                        COMMENT '字典名称',
  `dict_type`   varchar(100)   DEFAULT ''                        COMMENT '字典类型（唯一）',
  `status`      char(1)         DEFAULT '0'                       COMMENT '状态（0=正常 1=停用）',
  `create_by`   bigint(20)      DEFAULT NULL                     COMMENT '创建者',
  `create_time` datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`   bigint(20)      DEFAULT NULL                     COMMENT '更新者',
  `update_time` datetime        DEFAULT NULL                     COMMENT '更新时间',
  `remark`      varchar(500)   DEFAULT NULL                      COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 10. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code`   bigint(20)      NOT NULL                          COMMENT '字典编码',
  `dict_sort`   int(4)          DEFAULT 0                        COMMENT '字典排序',
  `dict_label`  varchar(100)   DEFAULT ''                        COMMENT '字典标签',
  `dict_value`  varchar(100)   DEFAULT ''                        COMMENT '字典键值',
  `dict_type`   varchar(100)   DEFAULT ''                        COMMENT '字典类型',
  `css_class`   varchar(100)   DEFAULT NULL                     COMMENT '样式属性',
  `list_class`  varchar(100)   DEFAULT NULL                     COMMENT '表格回显样式',
  `is_default`  char(1)         DEFAULT 'N'                      COMMENT '是否默认（Y=是 N=否）',
  `status`      char(1)         DEFAULT '0'                      COMMENT '状态（0=正常 1=停用）',
  `create_by`   bigint(20)      DEFAULT NULL                     COMMENT '创建者',
  `create_time` datetime        DEFAULT NULL                     COMMENT '创建时间',
  `update_by`   bigint(20)      DEFAULT NULL                     COMMENT '更新者',
  `update_time` datetime        DEFAULT NULL                     COMMENT '更新时间',
  `remark`      varchar(500)   DEFAULT NULL                      COMMENT '备注',
  PRIMARY KEY (`dict_code`),
  KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 11. 操作日志记录表
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id`         bigint(20)      NOT NULL                          COMMENT '日志主键',
  `title`           varchar(50)    DEFAULT ''                        COMMENT '模块标题',
  `business_type`   int(2)          DEFAULT 0                        COMMENT '业务类型（0=其它 1=新增 2=修改 3=删除 4=授权 5=导出 6=导入 7=强退 8=生成代码 9=清空数据）',
  `method`          varchar(100)   DEFAULT ''                        COMMENT '方法名称（类名.方法名）',
  `request_method`  varchar(10)   DEFAULT ''                        COMMENT '请求方式（GET/POST/PUT/DELETE）',
  `operator_type`   int(1)         DEFAULT 0                        COMMENT '操作类别（0=其它 1=后台用户 2=手机端用户）',
  `oper_name`       varchar(50)    DEFAULT ''                        COMMENT '操作人员',
  `dept_name`       varchar(50)    DEFAULT ''                        COMMENT '部门名称',
  `oper_url`        varchar(255)   DEFAULT ''                        COMMENT '请求URL',
  `oper_ip`         varchar(128)   DEFAULT ''                        COMMENT '操作地点IP',
  `oper_location`   varchar(255)   DEFAULT ''                        COMMENT '操作地点',
  `oper_param`      varchar(2000)  DEFAULT ''                        COMMENT '请求参数（JSON）',
  `json_result`     varchar(2000)  DEFAULT ''                        COMMENT '返回参数（JSON）',
  `status`          int(1)          DEFAULT 0                        COMMENT '操作状态（0=正常 1=异常）',
  `error_msg`       varchar(2000)  DEFAULT ''                        COMMENT '错误消息',
  `oper_time`       datetime        DEFAULT NULL                     COMMENT '操作时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';

-- ----------------------------
-- 12. 系统访问日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id`        bigint(20)      NOT NULL                          COMMENT '访问ID（主键）',
  `user_name`      varchar(50)    DEFAULT ''                        COMMENT '用户账号',
  `ipaddr`         varchar(128)   DEFAULT ''                        COMMENT '登录IP地址',
  `login_location` varchar(255)   DEFAULT ''                        COMMENT '登录地点',
  `browser`         varchar(50)   DEFAULT ''                        COMMENT '浏览器类型',
  `os`             varchar(50)   DEFAULT ''                        COMMENT '操作系统',
  `status`         char(1)        DEFAULT '0'                       COMMENT '登录状态（0=成功 1=失败）',
  `msg`            varchar(255)   DEFAULT ''                        COMMENT '提示消息',
  `login_time`     datetime        DEFAULT NULL                     COMMENT '登录时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问日志表';

-- =============================================================
-- 预置数据
-- =============================================================

-- ----------------------------
-- 预置部门（顶级集团）
-- ----------------------------
INSERT INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader_user_id`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (103, 0, '0', 'Scaffold集团', 0, 1, '15888888888', 'admin@scaffold.com', '0', '0', 1, NOW(), NULL, NULL);

-- ----------------------------
-- 预置角色
-- ----------------------------
-- 超级管理员（拥有所有权限）
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', 1, NOW(), NULL, NULL, '0', '超级管理员，拥有所有权限');

-- 普通角色（仅能查看）
INSERT INTO `sys_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `menu_check_strictly`, `dept_check_strictly`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (2, '普通角色', 'common', 2, '5', 1, 1, '0', 1, NOW(), NULL, NULL, '0', '普通角色，仅能查看数据');

-- ----------------------------
-- 预置用户（密码: admin，BCrypt加密）
-- 注意：生产环境请务必修改默认密码！
-- BCrypt hash for "admin": $2a$10$Skbs1o3oqDKWn96IDanZseuvy96ix5LcE2.m618Pxqxw2CBIjCx.O
-- ----------------------------
INSERT INTO `sys_user` (`user_id`, `dept_id`, `user_name`, `nick_name`, `user_type`, `email`, `phone_number`, `sex`, `avatar`, `password`, `status`, `login_ip`, `login_date`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (1, 103, 'admin', '管理员', '00', 'admin@scaffold.com', '15888888888', '0', '', '$2a$10$Skbs1o3oqDKWn96IDanZseuvy96ix5LcE2.m618Pxqxw2CBIjCx.O', '0', '127.0.0.1', NOW(), 103, 1, NOW(), NULL, NULL, '0', '管理员账号');

-- ----------------------------
-- 预置菜单（系统管理目录 + 子菜单/按钮）
-- ----------------------------
-- M 目录
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 1, NOW(), NULL, NULL, '0', '系统管理目录');

-- C 菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (2, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 1, NOW(), NULL, NULL, '0', '用户管理菜单');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (3, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 1, NOW(), NULL, NULL, '0', '角色管理菜单');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (4, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 1, NOW(), NULL, NULL, '0', '菜单管理菜单');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (5, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 1, NOW(), NULL, NULL, '0', '部门管理菜单');

-- F 按钮（用户管理）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (100, '用户新增', 2, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 1, NOW(), NULL, NULL, '0', '用户新增按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (101, '用户编辑', 2, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 1, NOW(), NULL, NULL, '0', '用户编辑按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (102, '用户删除', 2, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 1, NOW(), NULL, NULL, '0', '用户删除按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (103, '用户导出', 2, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 1, NOW(), NULL, NULL, '0', '用户导出按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (104, '用户导入', 2, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 1, NOW(), NULL, NULL, '0', '用户导入按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (105, '用户重置密码', 2, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 1, NOW(), NULL, NULL, '0', '用户重置密码按钮');

-- F 按钮（角色管理）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (106, '角色新增', 3, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 1, NOW(), NULL, NULL, '0', '角色新增按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (107, '角色编辑', 3, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 1, NOW(), NULL, NULL, '0', '角色编辑按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (108, '角色删除', 3, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 1, NOW(), NULL, NULL, '0', '角色删除按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (109, '角色导出', 3, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 1, NOW(), NULL, NULL, '0', '角色导出按钮');

-- F 按钮（菜单管理）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (110, '菜单新增', 4, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 1, NOW(), NULL, NULL, '0', '菜单新增按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (111, '菜单编辑', 4, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 1, NOW(), NULL, NULL, '0', '菜单编辑按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (112, '菜单删除', 4, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 1, NOW(), NULL, NULL, '0', '菜单删除按钮');

-- F 按钮（部门管理）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (113, '部门新增', 5, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 1, NOW(), NULL, NULL, '0', '部门新增按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (114, '部门编辑', 5, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 1, NOW(), NULL, NULL, '0', '部门编辑按钮');

INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `remark`)
VALUES (115, '部门删除', 5, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 1, NOW(), NULL, NULL, '0', '部门删除按钮');

-- ----------------------------
-- 预置用户角色关联
-- ----------------------------
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- ----------------------------
-- 预置角色菜单关联（超级管理员拥有所有菜单权限）
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 1);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 2);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 3);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 4);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 5);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 100);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 101);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 102);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 103);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 104);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 105);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 106);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 107);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 108);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 109);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 110);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 111);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 112);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 113);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 114);
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, 115);

-- ----------------------------
-- 预置 OSS 配置（MinIO）
-- ----------------------------
INSERT INTO `sys_oss_config` (`oss_id`, `tenant_id`, `provider`, `access_key`, `secret_key`, `bucket_name`, `prefix`, `endpoint`, `domain`, `is_https`, `access_policy`, `status`, `remark`, `create_dept`, `create_by`, `create_time`, `update_by`, `update_time`)
VALUES (1, '000000', 'minio', 'admin', 'admin123', 'scaffold', 'uploads', '127.0.0.1:9000', '', 'N', '1', '0', 'MinIO 对象存储配置', 103, 1, NOW(), NULL, NULL);

-- ----------------------------
-- 预置字典类型
-- ----------------------------
INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (1, '用户性别', 'sys_user_sex', '0', 1, NOW(), NULL, NULL, '用户性别列表');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2, '菜单状态', 'sys_show_hide', '0', 1, NOW(), NULL, NULL, '菜单状态列表');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (3, '操作日志状态', 'sys_oper_status', '0', 1, NOW(), NULL, NULL, '操作日志状态列表');

INSERT INTO `sys_dict_type` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4, '系统开关', 'sys_normal_disable', '0', 1, NOW(), NULL, NULL, '系统开关列表');

-- ----------------------------
-- 预置字典数据
-- ----------------------------
-- 用户性别
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (1, 1, '男', '0', 'sys_user_sex', '', 'primary', 'Y', '0', 1, NOW(), NULL, NULL, '男性');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2, 2, '女', '1', 'sys_user_sex', '', 'danger', 'N', '0', 1, NOW(), NULL, NULL, '女性');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (3, 3, '未知', '2', 'sys_user_sex', '', 'info', 'N', '0', 1, NOW(), NULL, NULL, '未知');

-- 菜单状态
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 1, NOW(), NULL, NULL, '显示菜单');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 1, NOW(), NULL, NULL, '隐藏菜单');

-- 操作日志状态
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (6, 1, '正常', '0', 'sys_oper_status', '', 'primary', 'Y', '0', 1, NOW(), NULL, NULL, '正常操作');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (7, 2, '异常', '1', 'sys_oper_status', '', 'danger', 'N', '0', 1, NOW(), NULL, NULL, '异常操作');

-- 系统开关
INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (8, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 1, NOW(), NULL, NULL, '正常状态');

INSERT INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (9, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 1, NOW(), NULL, NULL, '停用状态');

SET FOREIGN_KEY_CHECKS = 1;
