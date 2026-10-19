package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典数据信息（Dubbo RPC 传输对象）。
 *
 *  @author 8800
 */
@Data
public class DubboDictDataVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典编码 */
    private Long dictCode;

    /** 字典排序 */
    private Integer dictSort;

    /** 字典标签 */
    private String dictLabel;

    /** 字典键值 */
    private String dictValue;

    /** 字典类型 */
    private String dictType;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认（Y=是 N=否） */
    private String isDefault;

    /** 状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;
}
