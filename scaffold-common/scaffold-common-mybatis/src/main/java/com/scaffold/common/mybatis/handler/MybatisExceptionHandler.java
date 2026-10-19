package com.scaffold.common.mybatis.handler;

import com.scaffold.common.core.constant.HttpStatus;
import com.scaffold.common.core.model.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * MyBatis 异常处理器。
 */
@Slf4j
@RestControllerAdvice
public class MybatisExceptionHandler {

    /** 唯一键冲突（UNIQUE 约束重复）。 */
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.error("请求地址 '{}', 数据记录已存在: {}", request.getRequestURI(), e.getMessage());
        return Result.fail(HttpStatus.CONFLICT, "数据已存在，请勿重复添加");
    }

    /** 数据访问异常（SQL 语法错误、字段不匹配等）。 */
    @ExceptionHandler(DataAccessException.class)
    public Result<Void> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error("请求地址 '{}', 数据访问异常", request.getRequestURI(), e);
        return Result.fail(HttpStatus.ERROR, "数据库操作异常，请联系管理员");
    }

    /** MyBatis 系统异常。 */
    @ExceptionHandler(MyBatisSystemException.class)
    public Result<Void> handleMyBatisSystemException(MyBatisSystemException e, HttpServletRequest request) {
        log.error("请求地址 '{}', MyBatis 系统异常", request.getRequestURI(), e);
        return Result.fail(HttpStatus.ERROR, "数据库查询异常，请稍后重试");
    }
}
