package com.jmall.api.pay.feign;

import com.jmall.api.pay.dto.PayResultDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 支付服务远程调用接口
 * <p>
 * 供 order 服务调用，发起支付和查询支付结果。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-pay", contextId = "remotePayService")
public interface RemotePayService {

    /**
     * 发起支付
     *
     * @param orderNo  订单号
     * @param payType  支付方式
     * @param amount   支付金额
     * @return 支付结果
     */
    @PostMapping("/api/pay/inner/create")
    Result<PayResultDTO> createPayment(@RequestParam("orderNo") String orderNo,
                                       @RequestParam("payType") Integer payType,
                                       @RequestParam("amount") BigDecimal amount);

    /**
     * 查询支付结果
     *
     * @param orderNo 订单号
     * @return 支付结果
     */
    @GetMapping("/api/pay/inner/{orderNo}")
    Result<PayResultDTO> getPayResult(@PathVariable("orderNo") String orderNo);

    /**
     * 发起退款
     *
     * @param orderNo 订单号
     * @return 结果
     */
    @PostMapping("/api/pay/inner/{orderNo}/refund")
    Result<Boolean> refund(@PathVariable("orderNo") String orderNo);
}
