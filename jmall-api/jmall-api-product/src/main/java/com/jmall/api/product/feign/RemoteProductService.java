package com.jmall.api.product.feign;

import com.jmall.api.product.dto.SkuDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 商品服务远程调用接口
 * <p>
 * 供 cart、order、search 等服务调用，查询商品和 SKU 信息。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-product", contextId = "remoteProductService")
public interface RemoteProductService {

    /**
     * 根据 SKU ID 查询 SKU 信息
     *
     * @param skuId SKU ID
     * @return SKU 信息
     */
    @GetMapping("/api/product/inner/sku/{skuId}")
    Result<SkuDTO> getSkuById(@PathVariable("skuId") Long skuId);

    /**
     * 批量查询 SKU 信息
     *
     * @param skuIds SKU ID 列表
     * @return SKU 信息列表
     */
    @PostMapping("/api/product/inner/sku/list")
    Result<List<SkuDTO>> listSkuByIds(@RequestBody List<Long> skuIds);

    /**
     * 校验商品是否上架
     *
     * @param skuId SKU ID
     * @return true=已上架
     */
    @GetMapping("/api/product/inner/sku/{skuId}/status")
    Result<Boolean> checkSkuOnSale(@PathVariable("skuId") Long skuId);
}
