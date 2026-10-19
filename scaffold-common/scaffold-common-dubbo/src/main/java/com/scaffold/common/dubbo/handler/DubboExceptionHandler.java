package com.scaffold.common.dubbo.handler;

import com.scaffold.common.core.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Dubbo 异常处理器。
 */
@Slf4j
@RestControllerAdvice
public class DubboExceptionHandler {

    /**
     * RPC 调用异常。
     */
    @ExceptionHandler(RpcException.class)
    public Result<Void> handleDubboException(RpcException e) {
        log.error("RPC 异常: {}", e.getMessage(), e);
        if (e.isTimeout()) {
            return Result.failed("服务响应超时，请稍后再试");
        }
        if (e.isNetwork()) {
            return Result.failed("服务网络异常，请稍后再试");
        }
        return Result.failed("服务调用异常，请稍后再试");
    }
}
