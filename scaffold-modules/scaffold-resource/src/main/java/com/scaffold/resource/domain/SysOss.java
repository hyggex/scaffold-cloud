package com.scaffold.resource.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.scaffold.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * OSS 对象存储实体
 *
 * @author 8800
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss")
public class SysOss extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    @TableId(value = "oss_id")
    private Long ossId;

    /**
     * 文件名（对象键）
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
     * 扩展字段（JSON，存储文件大小、MIME类型等）
     */
    private String ext1;
}
