package com.jmall.api.merchant.feign;

import com.jmall.api.merchant.dto.ShopDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 商家服务远程调用接口
 * <p>
 * 供 product、order 等服务调用，查询店铺信息。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-merchant", contextId = "remoteMerchantService")
public interface RemoteMerchantService {

    /**
     * 根据商家ID查询店铺信息
     *
     * @param merchantId 商家ID
     * @return 店铺信息
     */
    @GetMapping("/api/merchant/inner/{merchantId}")
    Result<ShopDTO> getShopByMerchantId(@PathVariable("merchantId") Long merchantId);

    /**
     * 根据店铺ID查询店铺信息
     *
     * @param shopId 店铺ID
     * @return 店铺信息
     */
    @GetMapping("/api/merchant/inner/shop/{shopId}")
    Result<ShopDTO> getShopById(@PathVariable("shopId") Long shopId);

    /**
     * 校验商家是否已通过审核
     *
     * @param merchantId 商家ID
     * @return true=已审核
     */
    @GetMapping("/api/merchant/inner/{merchantId}/approved")
    Result<Boolean> checkMerchantApproved(@PathVariable("merchantId") Long merchantId);
}
