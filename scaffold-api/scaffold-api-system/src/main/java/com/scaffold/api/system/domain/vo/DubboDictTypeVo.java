package com.scaffold.api.system.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 字典类型信息（Dubbo RPC 传输对象）。
 *
 *  @author 8800
 */
@Data
public class DubboDictTypeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 字典主键 */
    private Long dictId;

    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;

    /** 状态（0=正常 1=停用） */
    private String status;

    /** 备注 */
    private String remark;
}
