package com.scaffold.common.redis.handler;

import com.baomidou.lock.exception.LockFailureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Redis 异常处理器。
 */
@Slf4j
@RestControllerAdvice
public class RedisExceptionHandler {

    /** 分布式锁获取失败异常 */
    @ExceptionHandler(LockFailureException.class)
    public Object handleLockFailureException(LockFailureException e) {
        log.error("获取锁失败，发生 Lock4j 异常", e);
        return 503;
    }
}
