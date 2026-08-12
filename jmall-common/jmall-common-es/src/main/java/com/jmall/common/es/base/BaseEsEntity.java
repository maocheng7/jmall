package com.jmall.common.es.base;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ES 文档基类
 * <p>
 * 所有 ES 索引文档继承此类，统一包含主键。
 * </p>
 *
 * @author jmall
 */
@Data
public abstract class BaseEsEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文档主键ID
     */
    private String id;
}
