# Scaffold 微服务框架

<p align="center">
    <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2.9-blue.svg" alt="Spring Boot"></a>
    <a href="https://spring.io/projects/spring-cloud"><img src="https://img.shields.io/badge/Spring%20Cloud-2023.0.1.2-blue.svg" alt="Spring Cloud"></a>
    <a href="https://github.com/alibaba/spring-cloud-alibaba"><img src="https://img.shields.io/badge/Spring%20Cloud%20Alibaba-2023.0.1.2-blue.svg" alt="Spring Cloud Alibaba"></a>
    <a href="https://nacos.io"><img src="https://img.shields.io/badge/Nacos-2.3.2-blue.svg" alt="Nacos"></a>
    <a href="http://dubbo.apache.org"><img src="https://img.shields.io/badge/Dubbo-3.2.12-blue.svg" alt="Dubbo"></a>
    <a href="https://sa-token.dev"><img src="https://img.shields.io/badge/Sa--Token-1.39.0-blue.svg" alt="Sa-Token"></a>
    <a href="https://.baomidou.com"><img src="https://img.shields.io/badge/MyBatis--Plus-3.5.16-blue.svg" alt="MyBatis-Plus"></a>
    <a href="https://www.java.com"><img src="https://img.shields.io/badge/Java-17--blue.svg" alt="Java"></a>
</p>

---

## 1. 简介

Scaffold 是基于 Java 17 + Spring Boot 3 + Spring Cloud Alibaba 的轻量级微服务开发框架，参考 RuoYi 项目结构思想设计，适合个人开发者快速搭建中小型微服务系统。

### 特性

- **简洁至上**：仅保留业务必需的功能，不过度封装
- **开箱即用**：内置登录认证、权限控制、文件存储、分页查询等基础能力
- **模块化设计**：公共模块与业务模块分离，便于复用和扩展
- **技术栈主流**：Spring Boot 3 / Nacos / Gateway / Dubbo / MyBatis-Plus / Sa-Token / Redis
- **中文注释**：全部代码中文注释，便于理解和二次开发

---

## 2. 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Java | 17 |
| Spring Boot | Spring Boot | 3.2.9 |
| 微服务框架 | Spring Cloud Alibaba | 2023.0.1.2 |
| 注册/配置中心 | Nacos | 2.3.2 |
| API 网关 | Spring Cloud Gateway | 2023.0.1 |
| RPC 框架 | Dubbo | 3.2.12 |
| ORM | MyBatis-Plus | 3.5.16 |
| 缓存 | Redisson + Caffeine | 3.37 / 3.10 |
| 认证鉴权 | Sa-Token | 1.39.0 |
| 对象存储 | MinIO（AWS S3 协议） | - |
| 数据库 | MySQL | 8.0 |
| 构建工具 | Maven | 3.9+ |

---

## 3. 项目结构

```
scaffold/
├── scaffold-common/               # 公共能力聚合模块
│   ├── scaffold-common-core       # 核心工具：Result、异常、常量、工具类
│   ├── scaffold-common-web        # Web基础：全局异常处理、BaseController
│   ├── scaffold-common-mybatis   # MyBatis-Plus：分页、字段填充、BaseEntity
│   ├── scaffold-common-security   # 安全认证：Sa-Token + LoginHelper + LoginUser
│   ├── scaffold-common-redis      # 缓存：Redisson + Caffeine 二级缓存
│   ├── scaffold-common-dubbo     # Dubbo RPC 配置
│   ├── scaffold-common-oss        # 文件存储：MinIO (S3 协议)
│   ├── scaffold-common-json       # JSON：Jackson 配置、JsonUtils
│   └── scaffold-common-log        # 日志注解
├── scaffold-api/                   # 跨服务接口契约（Dubbo Provider）
│   └── scaffold-api-system        # 系统服务 API 接口
├── scaffold-gateway/              # 统一网关（端口 8080）
├── scaffold-modules/              # 业务服务聚合
│   ├── scaffold-system           # 系统服务（用户/角色/菜单/部门，端口 9201）
│   └── scaffold-resource         # 文件服务（MinIO 上传下载，端口 8082）
└── scaffold-nacos/              # 嵌入式 Nacos 2.3.2（端口 8848）
```

---

## 4. 快速开始

### 4.1 环境要求

- JDK 17+
- Maven 3.9+
- Docker（用于启动 MySQL、Redis、MinIO）

### 4.2 启动基础设施

```bash
cd script
docker compose up -d
```

启动后会创建：

| 服务 | 端口 | 账号 |
|------|------|------|
| MySQL | 3306 | root / root123 |
| Redis | 6379 | - |
| MinIO | 9000 / 9001 | admin / admin123 |

### 4.3 初始化数据库

```sql
-- 创建数据库
CREATE DATABASE scaffold DEFAULT CHARACTER SET utf8mb4;
USE scaffold;

-- 执行初始化脚本（将 script/sql/scaffold.sql 导入）
```

### 4.4 启动 Nacos

```bash
cd scaffold-nacos
mvn clean package -DskipTests
java -jar target/scaffold-nacos.jar
```

访问 http://localhost:8848/nacos（账号：nacos / nacos）

### 4.5 启动业务服务

**启动顺序：scaffold-system → scaffold-resource → scaffold-gateway**

```bash
# 系统服务（端口 9201）
cd scaffold-modules/scaffold-system
mvn spring-boot:run

# 文件服务（端口 8082）
cd scaffold-modules/scaffold-resource
mvn spring-boot:run

# API 网关（端口 8080）
cd scaffold-gateway
mvn spring-boot:run
```

---

## 5. 接口文档

启动服务后访问：

| 服务 | 地址 |
|------|------|
| Knife4j 在线文档 | http://localhost:8080/doc.html |
| Nacos 控制台 | http://localhost:8848/nacos |

---

## 6. 核心功能说明

### 6.1 登录认证

**登录接口：**
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "eyJ0b2tlbiI6IjEyMzQ1Njc4LTEyMzQ1Njc4LTEyMzQ1Njc4In0="
}
```

**在业务代码中获取当前登录用户：**

```java
// 获取当前登录用户 ID
Long userId = LoginHelper.getUserId();

// 获取登录用户完整信息
LoginUser loginUser = LoginHelper.getLoginUser();

// 获取部门 ID
Long deptId = LoginHelper.getDeptId();

// 判断是否超级管理员
boolean isAdmin = LoginHelper.isSuperAdmin();

// 判断是否有某个角色
boolean hasAdmin = LoginHelper.hasRole("admin");

// 判断是否有某个权限
boolean hasPerms = LoginHelper.hasPermission("system:user:list");

// 获取角色 key 集合
Set<String> roleKeys = LoginHelper.getRoleKeys();

// 获取权限标识集合
Set<String> permissions = LoginHelper.getPermissions();
```

### 6.2 权限控制

使用 `@SaCheckPermission` 注解进行接口级权限控制：

```java
@SaCheckPermission("system:user:list")
@GetMapping("/list")
public Result<List<SysUser>> list() {
    return Result.ok(userService.list());
}
```

### 6.3 数据自动填充

实体类继承 `BaseEntity` 后，创建和更新时会自动填充：

- `createBy` / `updateBy` — 创建人/更新人（自动获取当前登录用户 ID）
- `createTime` / `updateTime` — 创建时间/更新时间（自动填充）
- `createDept` — 创建部门

```java
@Data
public class SysUser extends BaseEntity {
    private Long userId;
    private String username;
    // ...
}
```

### 6.4 多表别名查询

使用 `AliasLambdaQueryWrapper` 支持多表联查时的别名自动拼接：

```java
// 设置别名，所有 Lambda 条件自动加上 "u." 前缀
AliasLambdaQueryWrapper<User> qw = AliasLambdaQueryWrapper.of("u");
qw.eq(User::getStatus, "0")           // -> u.status = '0'
  .like(User::getName, "张")           // -> u.name LIKE '%张%'
  .exists("SELECT 1 FROM sys_role r WHERE r.role_id = u.role_id");

// 获取带别名的列名（用于原生 SQL）
String nameCol = qw.getAliasedColumn(User::getName); // "u.name"
qw.apply("{} IS NOT NULL", nameCol);
```

### 6.5 分页查询

使用 `PageQuery` 进行分页查询：

```java
@GetMapping("/list")
public Result<TableDataInfo<SysUser>> list(SysUser user, PageQuery pageQuery) {
    IPage<SysUser> page = userService.selectPageList(user, pageQuery.build());
    return Result.ok(TableDataInfo.build(page));
}
```

### 6.6 文件上传

```java
@PostMapping("/upload")
public Result<SysOss> upload(@RequestParam("file") MultipartFile file) {
    return Result.ok(ossService.upload(file));
}
```

---

## 7. 登录接口说明

框架使用 BCrypt 密码加密。数据库预置管理员账号：

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 超级管理员 |

> ⚠️ 生产环境请务必修改默认密码。

---

## 8. 数据库表说明

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| sys_role | 角色表 |
| sys_menu | 菜单权限表 |
| sys_user_role | 用户角色关联表 |
| sys_role_menu | 角色菜单关联表 |
| sys_dept | 部门表 |
| sys_dict_type | 字典类型表 |
| sys_dict_data | 字典数据表 |
| sys_oper_log | 操作日志表 |
| sys_logininfor | 登录日志表 |
| sys_oss | OSS 对象存储表 |

---

## 9. 配置说明

### 9.1 Nacos 配置中心

生产环境建议将以下配置放入 Nacos，通过 `spring.config.import` 引用：

```yaml
spring:
  config:
    import:
      - optional:nacos:application-common.yml    # 公共配置
      - optional:nacos:datasource.yml            # 数据源配置
      - optional:nacos:scaffold-system.yml      # 系统服务配置
```

### 9.2 核心配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `MYSQL_HOST` | MySQL 地址 | localhost |
| `MYSQL_PORT` | MySQL 端口 | 3306 |
| `MYSQL_DB` | 数据库名 | scaffold |
| `MYSQL_USER` | 数据库账号 | root |
| `MYSQL_PASSWORD` | 数据库密码 | root123 |
| `REDIS_HOST` | Redis 地址 | localhost |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `NACOS_SERVER_ADDR` | Nacos 地址 | localhost:8848 |

### 9.3 MinIO 配置

```yaml
oss:
  minio:
    enabled: true
    endpoint: 127.0.0.1:9000
    bucket-name: scaffold
    prefix: uploads
    access-key: admin
    secret-key: admin123
    is-https: N
    access-policy: 1  # 0=private, 1=public, 2=custom
```

---

## 10. 新增业务模块

参考 `scaffold-system` 或 `scaffold-resource`，步骤如下：

```bash
# 1. 复制 scaffold-system 目录为新模块
cp -r scaffold-modules/scaffold-system scaffold-modules/scaffold-xxx

# 2. 修改新模块 pom.xml 的 artifactId 和主类名

# 3. 修改 application.yml 的 spring.application.name 和端口

# 4. 修改主类的 @MapperScan 包路径

# 5. 在 scaffold-modules/pom.xml 中添加新模块

# 6. 在 scaffold-gateway/application.yml 中添加路由规则：
#    - id: scaffold-xxx
#      uri: lb://scaffold-xxx
#      predicates:
#        - Path=/xxx/**
#      filters:
#        - StripPrefix=1
```

---

## 11. 常见问题

### Q: 编译报错 "package does not exist"
确保使用 JDK 17+，并在项目根目录执行：
```bash
mvn clean compile -DskipTests
```

### Q: 登录提示"用户不存在"
检查数据库中 `sys_user` 表是否有数据，预置 SQL 请参考 `script/sql/scaffold.sql`。

### Q: Nacos 注册失败
检查 `application.yml` 中 `spring.cloud.nacos.discovery.server-addr` 配置是否正确，确保 Nacos 已启动。

### Q: 上传文件失败
检查 MinIO 是否已启动，控制台访问 http://localhost:9001 创建名为 `scaffold` 的存储桶。

---

## 12. 参与贡献

1. Fork 本仓库
2. 新建 Feature 分支 (`git checkout -b feature/AmazingFeature`)
3. 提交代码 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 13. 开源协议

本项目采用 MIT 开源协议，您可以自由使用、修改和分发。
