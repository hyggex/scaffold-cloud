package com.scaffold.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scaffold.common.mybatis.core.page.PageQuery;
import com.scaffold.common.mybatis.core.page.TableDataInfo;
import com.scaffold.resource.domain.SysOss;
import com.scaffold.resource.domain.SysOssBo;
import com.scaffold.resource.domain.SysOssVo;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * 文件上传 Service 接口
 *
 * @author 8800
 */
public interface ISysOssService {

    /**
     * 查询 OSS 对象存储分页列表
     *
     * @param bo        查询对象
     * @param pageQuery 分页查询
     * @return 分页结果
     */
    TableDataInfo<SysOssVo> queryPageList(SysOssBo bo, PageQuery pageQuery);

    /**
     * 根据 ID 查询详情
     *
     * @param ossId OSS 主键
     * @return OSS 详情
     */
    SysOssVo getById(Long ossId);

    /**
     * 根据 ID 列表查询
     *
     * @param ossIds OSS 主键列表
     * @return OSS 列表
     */
    List<SysOssVo> listByIds(Collection<Long> ossIds);

    /**
     * 上传文件
     *
     * @param file 上传文件
     * @return 上传结果
     */
    SysOssVo upload(org.springframework.web.multipart.MultipartFile file);

    /**
     * 上传文件（本地 File）
     *
     * @param file 本地文件
     * @return 上传结果
     */
    SysOssVo upload(File file);

    /**
     * 删除 OSS 对象
     *
     * @param ids     OSS 主键列表
     * @param isValid 是否校验
     * @return 是否成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据 URL 删除文件（不记录数据库）
     *
     * @param fileUrl 文件 URL
     */
    void deleteByUrl(String fileUrl);
}
