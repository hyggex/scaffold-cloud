package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单信息（Dubbo RPC 传输对象）。
 *
 *  @author 8800
 */
@Data
public class DubboMenuVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    private Long menuId;

    /** 父菜单ID */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String queryParam;

    /** 是否外链（0是 1否） */
    private String isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    private String isCache;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 显示状态（0显示 1隐藏） */
    private String visible;

    /** 菜单状态（0正常 1停用） */
    private String status;

    /** 权限字符串 */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 父菜单名称 */
    private String parentName;

    /** 子菜单 */
    private List<DubboMenuVo> children;
}
