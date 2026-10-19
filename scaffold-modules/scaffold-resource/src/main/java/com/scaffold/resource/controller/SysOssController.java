package com.scaffold.resource.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.scaffold.common.core.model.Result;
import com.scaffold.common.core.validate.QueryGroup;
import com.scaffold.common.log.annotation.Log;
import com.scaffold.common.log.enums.BusinessType;
import com.scaffold.common.mybatis.core.page.PageQuery;
import com.scaffold.common.mybatis.core.page.TableDataInfo;
import com.scaffold.common.web.core.BaseController;
import com.scaffold.resource.domain.SysOssBo;
import com.scaffold.resource.domain.SysOssUploadVo;
import com.scaffold.resource.domain.SysOssVo;
import com.scaffold.resource.service.ISysOssService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 文件上传 Controller
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/oss")
public class SysOssController extends BaseController {

    private final ISysOssService iSysOssService;

    /**
     * 查询 OSS 对象存储列表
     */
    @SaCheckPermission("system:oss:list")
    @GetMapping("/list")
    public TableDataInfo<SysOssVo> list(SysOssBo bo, PageQuery pageQuery) {
        return iSysOssService.queryPageList(bo, pageQuery);
    }

    /**
     * 根据 ID 列表查询
     *
     * @param ossIds OSS 对象 ID 列表
     */
    @SaCheckPermission("system:oss:query")
    @GetMapping("/listByIds")
    public Result<List<SysOssVo>> listByIds(@NotEmpty(message = "主键不能为空") @RequestParam Long[] ossIds) {
        return Result.ok(iSysOssService.listByIds(Arrays.asList(ossIds)));
    }

    /**
     * 上传文件
     *
     * @param file 文件
     */
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<SysOssUploadVo> upload(@RequestPart("file") MultipartFile file) {
        SysOssVo oss = iSysOssService.upload(file);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileUrl(oss.getFileUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(String.valueOf(oss.getOssId()));
        return Result.ok(uploadVo);
    }

    /**
     * 下载文件
     *
     * @param ossId    OSS 对象 ID
     * @param response 响应
     */
    @SaCheckPermission("system:oss:download")
    @GetMapping("/download/{ossId}")
    public void download(@PathVariable Long ossId, HttpServletResponse response) throws IOException {
        iSysOssService.getById(ossId);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=download");
    }

    /**
     * 删除 OSS 对象
     *
     * @param ossIds OSS 对象 ID 列表
     */
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    public Result<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossIds) {
        return toAjax(iSysOssService.deleteWithValidByIds(Arrays.asList(ossIds), true));
    }
}
