package com.scaffold.resource.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件扩展信息（存储在 sys_oss.ext1 JSON 字段中）
 *
 * @author 8800
 */
@Data
public class SysOssExt implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件大小（单位：字节）
     */
    private Long fileSize;

    /**
     * 文件 MIME 类型
     */
    private String contentType;
}
