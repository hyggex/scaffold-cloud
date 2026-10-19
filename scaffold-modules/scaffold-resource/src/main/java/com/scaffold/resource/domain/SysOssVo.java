package com.scaffold.resource.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * OSS 对象 VO
 *
 * @author 8800
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SysOssVo {

    /**
     * 对象存储主键
     */
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原文件名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL 地址
     */
    private String url;

    /**
     * 文件路径（无前缀）
     */
    private String fileUrl;

    /**
     * 服务商
     */
    private String service;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * MIME 类型
     */
    private String contentType;
}
