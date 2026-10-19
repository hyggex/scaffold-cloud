package com.scaffold.common.web.core;

import com.scaffold.common.core.model.Result;

/**
 * Controller 基类。
 *
 * <p>提供统一的响应封装方法，所有 Controller 继承此类即可使用。</p>
 */
public class BaseController {

    /**
     * 根据影响行数返回响应。
     *
     * @param rows 数据库影响行数
     * @return 操作成功/失败响应
     */
    protected Result<Void> toAjax(int rows) {
        return rows > 0 ? Result.ok() : Result.failed();
    }

    /**
     * 根据布尔结果返回响应。
     *
     * @param result 操作结果
     * @return 操作成功/失败响应
     */
    protected Result<Void> toAjax(boolean result) {
        return result ? Result.ok() : Result.failed();
    }
}
