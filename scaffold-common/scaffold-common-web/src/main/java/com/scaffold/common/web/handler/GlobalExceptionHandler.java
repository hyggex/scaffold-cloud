package com.scaffold.common.web.handler;

import com.scaffold.common.core.constant.HttpStatus;
import com.scaffold.common.core.exception.ServiceException;
import com.scaffold.common.core.model.Result;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器。
 *
 * <p>统一转换业务异常、参数校验异常和系统异常，避免各个 Controller 重复编写异常处理逻辑。</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常（ServiceException）。
     * 支持占位符格式化和错误码。
     */
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e) {
        log.warn("业务异常: {}", e.getMessage());
        if (e.getCode() != null) {
            return Result.fail(e.getCode(), e.getMessage());
        }
        return Result.failed(e.getMessage());
    }

    /**
     * 参数校验异常。
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public Result<Void> handleValidationException(Exception e) {
        log.warn("参数校验异常: {}", e.getMessage());
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException mve) {
            var fieldError = mve.getBindingResult().getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }
        } else if (e instanceof ConstraintViolationException cve) {
            var violation = cve.getConstraintViolations().iterator().next();
            if (violation != null) {
                message = violation.getMessage();
            }
        }
        return Result.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 非法参数异常。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数: {}", e.getMessage());
        return Result.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * 未预期的系统异常。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(HttpStatus.ERROR, "系统异常，请稍后重试");
    }
}
