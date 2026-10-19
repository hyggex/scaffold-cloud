package com.scaffold.common.oss.entity;

import lombok.Data;

/**
 * 文件上传结果
 *
 * @author 8800
 */
@Data
public class UploadResult {

    /**
     * 文件访问 URL
     */
    private String url;

    /**
     * 文件路径（不含域名）
     */
    private String fileUrl;

    /**
     * 文件名（对象键）
     */
    private String filename;

    /**
     * ETag（文件唯一标识）
     */
    private String eTag;

    public static UploadResult builder() {
        return new UploadResult();
    }

    public UploadResult url(String url) {
        this.url = url;
        return this;
    }

    public UploadResult fileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public UploadResult filename(String filename) {
        this.filename = filename;
        return this;
    }

    public UploadResult eTag(String eTag) {
        this.eTag = eTag;
        return this;
    }

    public UploadResult build() {
        return this;
    }
}
