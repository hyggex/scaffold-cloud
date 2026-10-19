package com.scaffold.resource.domain;

import lombok.Data;

/**
 * 文件上传返回 VO
 *
 * @author Lion Li
 */
@Data
public class SysOssUploadVo {

    /**
     * 文件访问 URL
     */
    private String url;

    /**
     * 文件路径（无前缀）
     */
    private String fileUrl;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * OSS 主键
     */
    private String ossId;
}
