package com.scaffold.common.oss.exception;

/**
 * OSS 异常
 *
 * @author 8800
 */
public class OssException extends RuntimeException {

    public OssException(String message) {
        super(message);
    }

    public OssException(String message, Throwable cause) {
        super(message, cause);
    }
}
