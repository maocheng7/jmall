package com.jmall.product.service;

import com.jmall.api.product.dto.SkuDTO;
import com.jmall.common.core.result.PageResult;
import com.jmall.product.dto.SpuSaveDTO;
import com.jmall.product.entity.ProductSku;
import com.jmall.product.entity.ProductSpu;
import com.jmall.product.vo.ProductDetailVO;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author jmall
 */
public interface ProductService {

    /** 发布/编辑商品（含SKU） */
    Long saveSpu(SpuSaveDTO dto);

    /** 商品详情 */
    ProductDetailVO getDetail(Long spuId);

    /** 分页查询（用户端：仅上架） */
    PageResult<ProductSpu> pageOnSale(Long categoryId, String keyword, int page, int size);

    /** 商家端分页 */
    PageResult<ProductSpu> pageByMerchant(Long merchantId, Integer status, int page, int size);

    /** 上架 */
    void onShelf(Long spuId);

    /** 下架 */
    void offShelf(Long spuId);

    /** 根据 SKU ID 查询 */
    ProductSku getSkuById(Long skuId);

    /** 批量查询 SKU */
    List<ProductSku> listSkuByIds(List<Long> skuIds);

    /** 校验 SKU 是否上架可售 */
    boolean checkSkuOnSale(Long skuId);

    /** SKU 转 DTO */
    SkuDTO toSkuDTO(ProductSku sku);
}
