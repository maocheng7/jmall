package com.jmall.product.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类树节点
 *
 * @author jmall
 */
@Data
public class CategoryTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String name;
    private Integer level;
    private Integer sort;
    private String icon;
    private String image;
    private Integer status;
    private List<CategoryTreeVO> children = new ArrayList<>();
}
