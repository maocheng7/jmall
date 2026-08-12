package com.jmall.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品发布/编辑 DTO（含 SKU 列表）
 *
 * @author jmall
 */
@Data
public class SpuSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull(message = "商家ID不能为空")
    private Long merchantId;

    private Long shopId;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private Long brandId;

    @NotBlank(message = "商品名称不能为空")
    private String name;

    private String subtitle;
    private String mainImage;
    private String images;
    private String detail;
    private String searchKeyword;

    /** SKU 列表 */
    private List<SkuItem> skus;

    @Data
    public static class SkuItem implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private String specValue;
        private String image;
        @NotNull(message = "价格不能为空")
        private BigDecimal price;
        private BigDecimal originalPrice;
        private BigDecimal costPrice;
        private BigDecimal weight;
        private Integer status;
    }
}
