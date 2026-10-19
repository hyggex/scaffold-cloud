# Scaffold 微服务脚手架

<p align="center">
    <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.2.9-blue" alt="Spring Boot"></a>
    <a href="https://nacos.io"><img src="https://img.shields.io/badge/Nacos-2.3.2-blue" alt="Nacos"></a>
    <a href="http://dubbo.apache.org"><img src="https://img.shields.io/badge/Dubbo-3.2.12-blue" alt="Dubbo"></a>
    <a href="https://sa-token.dev"><img src="https://img.shields.io/badge/Sa--Token-1.39.0-blue" alt="Sa-Token"></a>
    <a href="https://.baomidou.com"><img src="https://img.shields.io/badge/MyBatis--Plus-3.5.16-blue" alt="MyBatis-Plus"></a>
    <a href="https://www.java.com"><img src="https://img.shields.io/badge/Java-17-blue" alt="Java"></a>
</p>

> 轻量、可复用的个人微服务脚手架，基于 Java 17、Spring Boot 3、Spring Cloud Alibaba、Nacos、Gateway、Dubbo、MyBatis-Plus、Redis、Sa-Token。

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 基础框架 | Java 17 + Spring Boot | 3.2.9 |
| 微服务 | Spring Cloud Alibaba + Nacos | 2023.0.1.2 / 2.3.2 |
| 网关 | Spring Cloud Gateway | 2023.0.1 |
| RPC | Dubbo | 3.2.12 |
| ORM | MyBatis-Plus | 3.5.16 |
| 缓存 | Redisson + Caffeine | 3.37 / 3.10 |
| 认证 | Sa-Token | 1.39.0 |
| 存储 | MinIO（AWS S3 协议） | - |
| 数据库 | MySQL | 8.0 |

## 模块结构

```
scaffold/
├── scaffold-common/               # 公共能力聚合
│   ├── scaffold-common-core       # 核心：Result、异常、常量、工具类
│   ├── scaffold-common-web        # Web：全局异常、BaseController
│   ├── scaffold-common-mybatis    # ORM：分页、字段填充、BaseEntity
│   ├── scaffold-common-security   # 安全：Sa-Token + LoginHelper
│   ├── scaffold-common-redis      # 缓存：Redisson + Caffeine 二级缓存
│   ├── scaffold-common-dubbo     # Dubbo：RPC 配置
│   ├── scaffold-common-oss        # 存储：MinIO (S3)
│   ├── scaffold-common-json       # JSON：Jackson 配置、JsonUtils
│   └── scaffold-common-log        # 日志：@Log 注解
├── scaffold-api/                   # 跨服务接口契约
│   └── scaffold-api-system        # 系统服务 API
├── scaffold-gateway/              # 统一网关（端口 8080）
├── scaffold-modules/              # 业务服务聚合
│   ├── scaffold-system           # 系统服务（端口 9201）
│   └── scaffold-resource         # 文件服务（端口 8082）
└── scaffold-nacos/              # 嵌入式 Nacos（端口 8848）
```

## 快速启动

> **注意**：首次运行需要完整执行以下步骤。之后日常启动只需从「步骤 3」开始。

### 1. 启动基础设施（Docker）

```bash
docker compose up -d
```

启动 MySQL（3306）、Redis（6379）、MinIO（9000/9001）。

### 2. 初始化数据库（一次性）

执行 `script/sql/scaffold.sql` 初始化数据库结构和预置数据。

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS scaffold DEFAULT CHARACTER SET utf8mb4;"
mysql -uroot -p scaffold < script/sql/scaffold.sql
```

### 3. 导入 Nacos 配置（一次性）

```bash
# 启动 Nacos
cd scaffold-nacos
mvn clean package -DskipTests
java -jar target/scaffold-nacos.jar &
```

访问 http://localhost:8848/nacos（账号：nacos / nacos）

进入「配置管理」→「配置列表」→「导入配置」，依次导入 `script/nacos/` 下的 5 个 YAML 文件（Data ID 保持默认）：

| Data ID | 说明 |
|---------|------|
| application-common.yml | 公共配置 |
| datasource.yml | 数据源配置 |
| scaffold-system.yml | 系统服务配置 |
| scaffold-gateway.yml | 网关配置 |
| scaffold-resource.yml | 文件服务配置 |

### 4. 启动业务服务

```bash
# 系统服务（端口 9201）
cd scaffold-modules/scaffold-system && mvn spring-boot:run

# 文件服务（端口 8082）
cd scaffold-modules/scaffold-resource && mvn spring-boot:run

# API 网关（端口 8080）
cd scaffold-gateway && mvn spring-boot:run
```

### 5. 验证部署

```bash
# 登录测试（账号：admin / admin）
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# 访问 Knife4j API 文档
# 系统服务：http://localhost:9201/doc.html
# 文件服务：http://localhost:8082/doc.html
```

## 核心功能

### 获取当前登录用户

```java
Long userId = LoginHelper.getUserId();         // 当前用户 ID
Long deptId = LoginHelper.getDeptId();         // 部门 ID
String deptName = LoginHelper.getDeptName();   // 部门名称
Set<String> roleKeys = LoginHelper.getRoleKeys();   // 角色集合
Set<String> perms = LoginHelper.getPermissions();    // 权限集合
boolean isAdmin = LoginHelper.isSuperAdmin();   // 是否超级管理员
boolean hasRole = LoginHelper.hasRole("admin"); // 是否有某角色
boolean hasPerms = LoginHelper.hasPermission("system:user:list"); // 是否有某权限
```

### 接口权限控制

```java
@SaCheckPermission("system:user:list")
@GetMapping("/list")
public Result<List<SysUser>> list() { ... }
```

### 分页查询

```java
@GetMapping("/list")
public Result<TableDataInfo<SysUser>> list(SysUser user, PageQuery pageQuery) {
    IPage<SysUser> page = userService.queryPage(user, pageQuery.build());
    return Result.ok(TableDataInfo.build(page));
}
```

### 多表别名查询

```java
// 所有 Lambda 条件自动拼接别名
AliasLambdaQueryWrapper<User> qw = AliasLambdaQueryWrapper.of("u");
qw.eq(User::getStatus, "0")   // -> u.status = '0'
  .like(User::getName, "张");  // -> u.name LIKE '%张%'
```

## 端口一览

| 服务 | 端口 |
|------|------|
| scaffold-gateway | 8080 |
| scaffold-system | 9201 |
| scaffold-resource | 8082 |
| scaffold-nacos | 8848 |
| MySQL | 3306 |
| Redis | 6379 |
| MinIO API | 9000 |
| MinIO Console | 9001 |

## 常见问题

### Q1: 启动报 "Unable to connect to Nacos"
确保 Nacos 已启动：`cd scaffold-nacos && java -jar target/scaffold-nacos.jar`

### Q2: 启动报 "Unable to connect to MySQL"
确保 Docker 容器运行中：`docker compose ps`

### Q3: 启动报 Redis 连接错误
Docker 中的 Redis 没有密码，检查是否有多余的 Redis 密码配置

### Q4: 登录失败，用户名密码错误
确认账号为 `admin` / `admin`（不是 admin123）

### Q5: 403 Forbidden
接口权限不足，使用 admin 账号重新登录获取新 Token

## 文档

详细使用文档请参考 `script/document.md`。

## License

MIT
