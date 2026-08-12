package com.jmall.search.service;

import com.jmall.common.core.result.PageResult;
import com.jmall.search.entity.ProductEsDoc;

import java.math.BigDecimal;

/**
 * 搜索服务接口
 *
 * @author jmall
 */
public interface SearchService {

    /**
     * 商品搜索
     *
     * @param keyword    关键词
     * @param categoryId 分类ID（可选）
     * @param brandId    品牌ID（可选）
     * @param minPrice   最低价（可选）
     * @param maxPrice   最高价（可选）
     * @param sort       排序：sales_desc/price_asc/price_desc/default
     * @param page       页码
     * @param size       每页大小
     */
    PageResult<ProductEsDoc> search(String keyword, Long categoryId, Long brandId,
                                    BigDecimal minPrice, BigDecimal maxPrice,
                                    String sort, int page, int size);

    /** 上架同步到 ES */
    void indexProduct(Long spuId);

    /** 下架从 ES 删除 */
    void deleteProduct(Long spuId);
}
