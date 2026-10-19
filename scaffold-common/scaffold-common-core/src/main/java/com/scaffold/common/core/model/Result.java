package com.scaffold.common.core.model;

import com.scaffold.common.core.constant.HttpStatus;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应对象。
 *
 * @param <T> 响应数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功状态码 */
    public static final int SUCCESS = HttpStatus.SUCCESS;

    /** 失败状态码 */
    public static final int FAIL = HttpStatus.ERROR;

    /** 状态码 */
    private int code;

    /** 消息 */
    private String message;

    /** 数据 */
    private T data;

    public Result() {}

    public static <T> Result<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> Result<T> ok(String message) {
        return restResult(null, SUCCESS, message);
    }

    public static <T> Result<T> ok(String message, T data) {
        return restResult(data, SUCCESS, message);
    }

    public static <T> Result<T> failed() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> Result<T> failed(String message) {
        return restResult(null, FAIL, message);
    }

    public static <T> Result<T> failed(T data) {
        return restResult(data, FAIL, "操作失败");
    }

    public static <T> Result<T> failed(String message, T data) {
        return restResult(data, FAIL, message);
    }

    public static <T> Result<T> fail(int code, String message) {
        return restResult(null, code, message);
    }

    /** 业务警告 */
    public static <T> Result<T> warn(String message) {
        return restResult(null, HttpStatus.WARN, message);
    }

    /** 业务警告（带数据） */
    public static <T> Result<T> warn(String message, T data) {
        return restResult(data, HttpStatus.WARN, message);
    }

    /** 判断是否为错误响应 */
    public static <T> boolean isError(Result<T> result) {
        return !isSuccess(result);
    }

    /** 判断是否为成功响应 */
    public static <T> boolean isSuccess(Result<T> result) {
        return result != null && result.getCode() == SUCCESS;
    }

    private static <T> Result<T> restResult(T data, int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        return result;
    }
}
