-- ----------------------------
-- OSS 对象存储表
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
