package com.jmall.api.coupon.feign;

import com.jmall.api.coupon.dto.CouponDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 优惠服务远程调用接口
 * <p>
 * 供 order 服务调用，查询优惠券信息和核销优惠券。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-coupon", contextId = "remoteCouponService")
public interface RemoteCouponService {

    /**
     * 查询用户优惠券信息
     *
     * @param userCouponId 用户优惠券ID
     * @return 优惠券信息
     */
    @GetMapping("/api/coupon/inner/{userCouponId}")
    Result<CouponDTO> getCoupon(@PathVariable("userCouponId") Long userCouponId);

    /**
     * 核销优惠券
     *
     * @param userCouponId 用户优惠券ID
     * @param orderNo      订单号
     * @return 结果
     */
    @PostMapping("/api/coupon/inner/{userCouponId}/use")
    Result<Boolean> useCoupon(@PathVariable("userCouponId") Long userCouponId,
                              @RequestParam("orderNo") String orderNo);

    /**
     * 退还优惠券（订单取消时调用）
     *
     * @param userCouponId 用户优惠券ID
     * @return 结果
     */
    @PostMapping("/api/coupon/inner/{userCouponId}/refund")
    Result<Boolean> refundCoupon(@PathVariable("userCouponId") Long userCouponId);

    /**
     * 计算优惠金额
     *
     * @param userCouponId 用户优惠券ID
     * @param orderAmount  订单金额
     * @return 优惠后金额
     */
    @GetMapping("/api/coupon/inner/{userCouponId}/calculate")
    Result<BigDecimal> calculateDiscount(@PathVariable("userCouponId") Long userCouponId,
                                         @RequestParam("orderAmount") BigDecimal orderAmount);
}
