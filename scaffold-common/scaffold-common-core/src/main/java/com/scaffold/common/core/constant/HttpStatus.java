package com.scaffold.common.core.constant;

/**
 * HTTP 状态码常量。
 */
public interface HttpStatus {

    /** 操作成功 */
    int SUCCESS = 200;

    /** 资源创建成功 */
    int CREATED = 201;

    /** 已接受请求，正在异步处理 */
    int ACCEPTED = 202;

    /** 无内容（删除成功等） */
    int NO_CONTENT = 204;

    /** 资源已被移除 */
    int MOVED_PERM = 301;

    /** 临时重定向 */
    int SEE_OTHER = 303;

    /** 参数错误 */
    int BAD_REQUEST = 400;

    /** 未授权 */
    int UNAUTHORIZED = 401;

    /** 禁止访问（无权限） */
    int FORBIDDEN = 403;

    /** 资源未找到 */
    int NOT_FOUND = 404;

    /** 方法不允许 */
    int BAD_METHOD = 405;

    /** 资源冲突（重复提交等） */
    int CONFLICT = 409;

    /** 不支持的媒体类型 */
    int UNSUPPORTED_TYPE = 415;

    /** 系统内部错误 */
    int ERROR = 500;

    /** 接口未实现 */
    int NOT_IMPLEMENTED = 501;

    /** 业务警告 */
    int WARN = 601;

    /** 库存不足 */
    int INVENTORY_SHORTAGE = 398;

    /** 需要用户确认的操作 */
    int CONFIRMATION = 399;
}
