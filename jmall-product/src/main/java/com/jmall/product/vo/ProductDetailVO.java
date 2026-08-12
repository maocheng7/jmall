package com.jmall.product.vo;

import com.jmall.product.entity.ProductSku;
import com.jmall.product.entity.ProductSpu;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 商品详情 VO（SPU + SKU 列表）
 *
 * @author jmall
 */
@Data
public class ProductDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ProductSpu spu;
    private List<ProductSku> skus;
}
