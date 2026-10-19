package com.scaffold.resource.domain;

import com.scaffold.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OSS 对象查询 BO
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOssBo extends BaseEntity {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原文件名
     */
    private String originalName;

    /**
     * 文件后缀
     */
    private String fileSuffix;

    /**
     * URL 地址
     */
    private String url;

    /**
     * 服务商
     */
    private String service;
}
