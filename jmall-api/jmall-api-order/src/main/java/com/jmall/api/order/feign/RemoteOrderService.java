package com.jmall.api.order.feign;

import com.jmall.api.order.dto.OrderDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 订单服务远程调用接口
 * <p>
 * 供 pay、merchant 等服务调用，查询和更新订单状态。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-order", contextId = "remoteOrderService")
public interface RemoteOrderService {

    /**
     * 根据订单号查询订单信息
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    @GetMapping("/api/order/inner/{orderNo}")
    Result<OrderDTO> getOrderByNo(@PathVariable("orderNo") String orderNo);

    /**
     * 更新订单状态为已支付
     *
     * @param orderNo 订单号
     * @return 结果
     */
    @PostMapping("/api/order/inner/{orderNo}/paid")
    Result<Boolean> updateOrderPaid(@PathVariable("orderNo") String orderNo);

    /**
     * 更新订单状态为已取消
     *
     * @param orderNo 订单号
     * @return 结果
     */
    @PostMapping("/api/order/inner/{orderNo}/cancel")
    Result<Boolean> updateOrderCancelled(@PathVariable("orderNo") String orderNo);
}
