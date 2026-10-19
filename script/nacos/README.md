# Nacos 配置说明

本目录包含 Scaffold 框架所有可托管到 Nacos 的配置文件。

## 方式一：通过 Nacos 控制台导入（推荐）

### 步骤

1. 启动 Nacos：
   ```bash
   cd scaffold-nacos
   java -jar target/scaffold-nacos.jar
   ```

2. 访问 Nacos 控制台：http://localhost:8848/nacos
   - 用户名：`nacos`
   - 密码：`nacos`

3. 进入「配置管理」→「配置列表」→「导入配置」

4. 依次导入以下 5 个 YAML 文件（Data ID 必须完全一致）：

| Data ID | 文件 | 说明 |
|---------|------|------|
| `application-common.yml` | `script/nacos/application-common.yml` | 公共配置（Sa-Token、日志） |
| `datasource.yml` | `script/nacos/datasource.yml` | 数据源配置（MySQL、Redis） |
| `scaffold-system.yml` | `script/nacos/scaffold-system.yml` | 系统服务配置 |
| `scaffold-gateway.yml` | `script/nacos/scaffold-gateway.yml` | 网关配置 |
| `scaffold-resource.yml` | `script/nacos/scaffold-resource.yml` | 文件服务配置 |

5. 导入时 Group 填写：`DEFAULT_GROUP`

---

## 方式二：通过 SQL 直接导入（MySQL 模式）

如果 Nacos 使用 MySQL 作为后端存储，可直接执行 SQL 脚本：

```bash
# 1. 先在 MySQL 中创建 Nacos 专用数据库（如果不存在）
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS nacos_config DEFAULT CHARACTER SET utf8mb4;"

# 2. 执行 Nacos 建表脚本（创建 config_info 表）
# 注意：如果是全新 Nacos MySQL 模式，请先执行 Nacos 自带的 MySQL schema 脚本
# 路径：scaffold-nacos/conf/mysql-schema.sql

# 3. 执行 Scaffold 配置导入
mysql -uroot -p nacos_config < script/nacos/nacos-config.sql
```

> ⚠️ 注意：必须是 Nacos 配置持久化到 MySQL 的情况才适用。如果使用默认 Derby 嵌入式数据库，请使用「方式一」导入。

---

## 环境变量说明

所有敏感配置支持环境变量覆盖，在运行服务前设置：

| 环境变量 | 说明 | 默认值 |
|---------|------|--------|
| `NACOS_SERVER_ADDR` | Nacos 地址 | `localhost:8848` |
| `NACOS_NAMESPACE` | Nacos 命名空间 | （空） |
| `NACOS_USERNAME` | Nacos 用户名 | `nacos` |
| `NACOS_PASSWORD` | Nacos 密码 | `nacos` |
| `MYSQL_HOST` | MySQL 地址 | `localhost` |
| `MYSQL_PORT` | MySQL 端口 | `3306` |
| `MYSQL_DB` | 数据库名 | `scaffold` |
| `MYSQL_USER` | 数据库账号 | `root` |
| `MYSQL_PASSWORD` | 数据库密码 | `root123` |
| `REDIS_HOST` | Redis 地址 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | （空） |
| `MINIO_ENDPOINT` | MinIO 地址 | `127.0.0.1:9000` |
| `MINIO_BUCKET` | MinIO 存储桶 | `scaffold` |
| `MINIO_ACCESS_KEY` | MinIO 访问密钥 | `admin` |
| `MINIO_SECRET_KEY` | MinIO 私有密钥 | `admin123` |

---

## 启动顺序

1. 启动 Nacos（8848）
2. 导入配置到 Nacos
3. 启动 MySQL + Redis + MinIO（Docker）
4. 初始化数据库：`mysql -uroot -p scaffold < script/sql/scaffold.sql`
5. 启动系统服务（9201）
6. 启动文件服务（8082）
7. 启动网关（8080）
