-- =============================================================
-- Nacos 2.x 配置数据导入脚本
-- 数据库：MySQL 8.0（Nacos 内置数据库或独立数据库）
-- 说明：执行后将以下配置直接写入 Nacos 的 config_info 表
-- 注意：NACOS_CONFIG_DB 通常为 Nacos 自身的数据库，默认为 nacos
--       如 Nacos 使用独立数据库，请先创建 nacos 数据库再执行
-- =============================================================

-- ----------------------------
-- Nacos 配置信息表
-- ----------------------------
-- 注意：如果 Nacos 使用的不是本地内置数据库，请修改下面的数据库名
--       默认 Nacos 使用 Derby 嵌入数据库，此 SQL 仅适用于 MySQL 数据库模式
--       如使用嵌入式数据库，请通过 Nacos 控制台（http://localhost:8848/nacos）导入 YAML 文件
-- ----------------------------

-- 创建 nacos_config 数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `nacos_config` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

USE `nacos_config`;

-- ----------------------------
-- Nacos config_info 表结构
-- ----------------------------
CREATE TABLE IF NOT EXISTS `config_info` (
  `id`            bigint(20)      NOT NULL AUTO_INCREMENT   COMMENT '主键ID',
  `data_id`       varchar(255)   NOT NULL                   COMMENT '配置ID（文件名）',
  `group_id`      varchar(255)   NOT NULL                   COMMENT '配置分组',
  `content`       longtext       NOT NULL                   COMMENT '配置内容',
  `md5`           varchar(32)     DEFAULT NULL               COMMENT 'MD5值',
  `gmt_create`    datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified`  datetime       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user`      text           DEFAULT NULL               COMMENT '源用户',
  `src_ip`        varchar(50)    DEFAULT NULL               COMMENT '源IP',
  `app_name`      varchar(128)   DEFAULT NULL               COMMENT '应用名',
  `tenant_id`     varchar(128)   DEFAULT ''                 COMMENT '租户ID',
  `c_desc`        varchar(256)   DEFAULT NULL               COMMENT '描述',
  `c_usr`         varchar(128)   DEFAULT NULL               COMMENT '��建人',
  `type`          varchar(32)    DEFAULT NULL               COMMENT '配置类型（yaml/yaml/xml/properties）',
  `c_id`          bigint(20)     DEFAULT NULL               COMMENT '主键',
  `encrypted_data_key` text      DEFAULT NULL               COMMENT '加密密钥',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`, `group_id`, `tenant_id`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='配置信息表';

-- ----------------------------
-- 公共配置：application-common.yml
-- ----------------------------
INSERT INTO `config_info` (`data_id`, `group_id`, `content`, `type`, `gmt_create`, `gmt_modified`, `c_desc`)
VALUES (
  'application-common.yml',
  'DEFAULT_GROUP',
  '# =============================================================
# Nacos 公共配置
# =============================================================

# Sa-Token 全局配置
sa-token:
  token-name: Authorization
  timeout: 86400
  active-timeout: -1
  is-concurrent: true
  is-share: false
  is-read-cookie: false
  is-read-header: true
  is-regex: true
  token-style: uuid
  is-log: false

# 日志级别配置
logging:
  level:
    root: info
    com.scaffold: debug
    com.baomidou.mybatisplus: warn
    org.springframework: warn
  pattern:
    console: ''%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{20} - [%method,%line] - %msg%n'''
  ',
  'yaml',
  NOW(),
  NOW(),
  '公共配置：Sa-Token + 日志级别'
);

-- ----------------------------
-- 数据源配置：datasource.yml
-- ----------------------------
INSERT INTO `config_info` (`data_id`, `group_id`, `content`, `type`, `gmt_create`, `gmt_modified`, `c_desc`)
VALUES (
  'datasource.yml',
  'DEFAULT_GROUP',
  '# =============================================================
# Nacos 数据源配置
# =============================================================

spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:scaffold}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:root123}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-timeout: 30000
      pool-name: ScaffoldHikariCP

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: ${REDIS_DB:0}
      timeout: 10000ms
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: -1ms
      ssl:
        enabled: false

mybatis-plus:
  global-config:
    db-config:
      id-type: assign_id
      logic-delete-field: delFlag
      logic-delete-value: "1"
      logic-not-delete-value: "0"
      db-type: mysql
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
  ',
  'yaml',
  NOW(),
  NOW(),
  '数据源配置：MySQL + Redis'
);

-- ----------------------------
-- 系统服务配置：scaffold-system.yml
-- ----------------------------
INSERT INTO `config_info` (`data_id`, `group_id`, `content`, `type`, `gmt_create`, `gmt_modified`, `c_desc`)
VALUES (
  'scaffold-system.yml',
  'DEFAULT_GROUP',
  '# =============================================================
# Nacos 配置：scaffold-system 系统服务
# =============================================================

spring:
  application:
    name: scaffold-system

dubbo:
  application:
    name: ${spring.application.name}
    qos-enable: false
    metadata-type: remote
  protocol:
    name: dubbo
    port: -1
    payload: 104857600
    threads: 500
    accepts: 3000
  registry:
    address: nacos://${NACOS_SERVER_ADDR:localhost:8848}
    group: DUBBO_GROUP
    username: ${NACOS_USERNAME:nacos}
    password: ${NACOS_PASSWORD:nacos}
    parameters:
      namespace: ${NACOS_NAMESPACE:}
    timeout: 5000
    register: true
    subscribe: true
  provider:
    preferred-networks: ${DUBBO_PREFERRED_NETWORKS:}
  consumer:
    timeout: 60000
    check: false
    retries: 0
  scan:
    base-packages: com.scaffold.**.dubbo
  custom:
    request-log: true
    log-level: info

redisson:
  key-prefix: ${SPRING_APPLICATION_NAME:}
  threads: 4
  nettyThreads: 8
  singleServerConfig:
    clientName: ${spring.application.name}
    connectionMinimumIdleSize: 8
    connectionPoolSize: 32
    idleConnectionTimeout: 10000
    timeout: 3000
    subscriptionConnectionPoolSize: 50
  ',
  'yaml',
  NOW(),
  NOW(),
  '系统服务配置：Dubbo + Redisson'
);

-- ----------------------------
-- 网关配置：scaffold-gateway.yml
-- ----------------------------
INSERT INTO `config_info` (`data_id`, `group_id`, `content`, `type`, `gmt_create`, `gmt_modified`, `c_desc`)
VALUES (
  'scaffold-gateway.yml',
  'DEFAULT_GROUP',
  '# =============================================================
# Nacos 配置：scaffold-gateway 网关服务
# =============================================================

spring:
  application:
    name: scaffold-gateway
  cloud:
    gateway:
      routes:
        - id: scaffold-system-auth
          uri: lb://scaffold-system
          predicates:
            - Path=/auth/**
        - id: scaffold-system-rest
          uri: lb://scaffold-system
          predicates:
            - Path=/system/**
          filters:
            - StripPrefix=1
        - id: scaffold-resource
          uri: lb://scaffold-resource
          predicates:
            - Path=/resource/**
          filters:
            - StripPrefix=1
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOriginPatterns: ''*''
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: ''*''
            allowCredentials: true
            maxAge: 3600
      loadbalancer:
        ribbon:
          enabled: false

dubbo:
  application:
    name: ${spring.application.name}
    qos-enable: false
    metadata-type: remote
  protocol:
    name: dubbo
    port: -1
  registry:
    address: nacos://${NACOS_SERVER_ADDR:localhost:8848}
    group: DUBBO_GROUP
    username: ${NACOS_USERNAME:nacos}
    password: ${NACOS_PASSWORD:nacos}
    parameters:
      namespace: ${NACOS_NAMESPACE:}
    timeout: 5000
    register: false
    subscribe: true
  consumer:
    check: false
  ',
  'yaml',
  NOW(),
  NOW(),
  '网关配置：路由规则 + CORS + Dubbo'
);

-- ----------------------------
-- 文件服务配置：scaffold-resource.yml
-- ----------------------------
INSERT INTO `config_info` (`data_id`, `group_id`, `content`, `type`, `gmt_create`, `gmt_modified`, `c_desc`)
VALUES (
  'scaffold-resource.yml',
  'DEFAULT_GROUP',
  '# =============================================================
# Nacos 配置：scaffold-resource 文件服务
# =============================================================

spring:
  application:
    name: scaffold-resource
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 100MB

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.scaffold.resource.domain
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

oss:
  minio:
    enabled: true
    endpoint: ${MINIO_ENDPOINT:127.0.0.1:9000}
    domain: ${MINIO_DOMAIN:}
    bucket-name: ${MINIO_BUCKET:scaffold}
    prefix: ${MINIO_PREFIX:uploads}
    access-key: ${MINIO_ACCESS_KEY:admin}
    secret-key: ${MINIO_SECRET_KEY:admin123}
    is-https: ${MINIO_IS_HTTPS:N}
    access-policy: ${MINIO_ACCESS_POLICY:1}
    region: ${MINIO_REGION:}
  ',
  'yaml',
  NOW(),
  NOW(),
  '文件服务配置：MinIO 对象存储'
);
