package com.scaffold.resource.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.scaffold.common.core.exception.ServiceException;
import com.scaffold.common.core.utils.StringUtils;
import com.scaffold.common.core.utils.file.FileUtils;
import com.scaffold.common.mybatis.core.page.PageQuery;
import com.scaffold.common.mybatis.core.page.TableDataInfo;
import com.scaffold.common.oss.core.MinioOssClient;
import com.scaffold.common.oss.entity.UploadResult;
import com.scaffold.resource.domain.SysOss;
import com.scaffold.resource.domain.SysOssBo;
import com.scaffold.resource.domain.SysOssExt;
import com.scaffold.resource.domain.SysOssVo;
import com.scaffold.resource.mapper.SysOssMapper;
import com.scaffold.resource.service.ISysOssService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件上传 Service 实现
 *
 * @author 8800
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl implements ISysOssService {

    private final SysOssMapper baseMapper;
    private final MinioOssClient minioOssClient;
    private final ObjectMapper objectMapper;

    /**
     * 分页查询 OSS 列表
     */
    @Override
    public TableDataInfo<SysOssVo> queryPageList(SysOssBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SysOss> lqw = buildQueryWrapper(bo);
        Page<SysOss> result = baseMapper.selectPage(pageQuery.build(), lqw);
        IPage<SysOssVo> voPage = result.convert(this::convertToVo);
        return TableDataInfo.build(voPage);
    }

    /**
     * 根据 ID 查询详情
     */
    @Cacheable(value = "sys_oss", key = "#ossId")
    @Override
    public SysOssVo getById(Long ossId) {
        SysOss oss = baseMapper.selectById(ossId);
        return convertToVo(oss);
    }

    /**
     * 根据 ID 列表查询
     */
    @Override
    public List<SysOssVo> listByIds(Collection<Long> ossIds) {
        if (CollectionUtil.isEmpty(ossIds)) {
            return List.of();
        }
        List<SysOss> list = baseMapper.selectBatchIds(ossIds);
        return list.stream().map(this::convertToVo).collect(Collectors.toList());
    }

    /**
     * 上传 MultipartFile
     */
    @Override
    public SysOssVo upload(MultipartFile file) {
        if (ObjectUtil.isNull(file) || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new ServiceException("上传文件名不能为空");
        }
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        UploadResult uploadResult;
        try {
            uploadResult = minioOssClient.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (Exception e) {
            throw new ServiceException("文件上传失败: " + e.getMessage());
        }

        // 构建扩展信息
        SysOssExt ext = new SysOssExt();
        ext.setFileSize(file.getSize());
        ext.setContentType(file.getContentType());

        return buildResultEntity(originalFilename, suffix, uploadResult, ext);
    }

    /**
     * 上传本地文件
     */
    @Override
    public SysOssVo upload(File file) {
        if (ObjectUtil.isNull(file) || !file.isFile() || file.length() <= 0) {
            throw new ServiceException("上传文件不能为空");
        }
        String originalFilename = file.getName();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        UploadResult uploadResult = minioOssClient.upload(file, minioOssClient.getPath(suffix), FileUtils.getMimeType(suffix));

        SysOssExt ext = new SysOssExt();
        ext.setFileSize(file.length());
        return buildResultEntity(originalFilename, suffix, uploadResult, ext);
    }

    /**
     * 删除文件（同时删除 OSS 和数据库记录）
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 业务校验（如需要）
        }
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        List<SysOss> list = baseMapper.selectBatchIds(ids);
        for (SysOss oss : list) {
            try {
                minioOssClient.delete(oss.getFileName());
            } catch (Exception e) {
                log.warn("删除 OSS 文件失败，key={}, error={}", oss.getFileName(), e.getMessage());
            }
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据 URL 删除（仅删除 OSS，不删数据库）
     */
    @Override
    public void deleteByUrl(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            return;
        }
        try {
            String key = fileUrl.substring(fileUrl.indexOf(minioOssClient.getBucketName()) + minioOssClient.getBucketName().length() + 1);
            minioOssClient.delete(key);
        } catch (Exception e) {
            log.warn("删除 OSS 文件失败，url={}, error={}", fileUrl, e.getMessage());
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<SysOss> buildQueryWrapper(SysOssBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SysOss> lqw = Wrappers.lambdaQuery();
        lqw.like(StrUtil.isNotBlank(bo.getFileName()), SysOss::getFileName, bo.getFileName());
        lqw.like(StrUtil.isNotBlank(bo.getOriginalName()), SysOss::getOriginalName, bo.getOriginalName());
        lqw.eq(StrUtil.isNotBlank(bo.getFileSuffix()), SysOss::getFileSuffix, bo.getFileSuffix());
        lqw.eq(StrUtil.isNotBlank(bo.getUrl()), SysOss::getUrl, bo.getUrl());
        lqw.eq(StrUtil.isNotBlank(bo.getService()), SysOss::getService, bo.getService());
        lqw.between(params.get("beginCreateTime") != null && params.get("endCreateTime") != null,
            SysOss::getCreateTime, params.get("beginCreateTime"), params.get("endCreateTime"));
        lqw.orderByDesc(SysOss::getCreateTime);
        return lqw;
    }

    /**
     * 构建结果实体并保存数据库
     */
    private SysOssVo buildResultEntity(String originalFilename, String suffix,
                                      UploadResult uploadResult, SysOssExt ext) {
        SysOss oss = new SysOss();
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalFilename);
        oss.setFileSuffix(suffix);
        oss.setUrl(uploadResult.getUrl());
        oss.setFileUrl(uploadResult.getFileUrl());
        oss.setService("minio");
        try {
            oss.setExt1(objectMapper.writeValueAsString(ext));
        } catch (Exception e) {
            log.warn("序列化扩展信息失败", e);
        }
        baseMapper.insert(oss);

        SysOssVo vo = convertToVo(oss);
        vo.setFileSize(ext.getFileSize());
        vo.setContentType(ext.getContentType());
        return vo;
    }

    /**
     * 转换为 VO
     */
    private SysOssVo convertToVo(SysOss oss) {
        if (ObjectUtil.isNull(oss)) {
            return null;
        }
        SysOssVo vo = new SysOssVo();
        vo.setOssId(oss.getOssId());
        vo.setFileName(oss.getFileName());
        vo.setOriginalName(oss.getOriginalName());
        vo.setFileSuffix(oss.getFileSuffix());
        vo.setUrl(oss.getUrl());
        vo.setFileUrl(oss.getFileUrl());
        vo.setService(oss.getService());

        // 解析扩展信息
        if (StrUtil.isNotBlank(oss.getExt1())) {
            try {
                SysOssExt ext = objectMapper.readValue(oss.getExt1(), SysOssExt.class);
                vo.setFileSize(ext.getFileSize());
                vo.setContentType(ext.getContentType());
            } catch (Exception e) {
                log.warn("解析 OSS 扩展信息失败, ext1={}", oss.getExt1());
            }
        }
        return vo;
    }
}
