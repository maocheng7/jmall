package com.jmall.order.controller;

import com.jmall.api.order.dto.OrderDTO;
import com.jmall.common.core.result.Result;
import com.jmall.order.entity.Order;
import com.jmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单内部接口（对齐 RemoteOrderService）
 *
 * @author jmall
 */
@Tag(name = "订单内部接口")
@RestController
@RequestMapping("/api/order/inner")
@RequiredArgsConstructor
public class OrderInnerController {

    private final OrderService orderService;

    @Operation(summary = "按订单号查询")
    @GetMapping("/{orderNo}")
    public Result<OrderDTO> getOrderByNo(@PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        return Result.success(orderService.toDTO(order));
    }

    @Operation(summary = "标记已支付")
    @PostMapping("/{orderNo}/paid")
    public Result<Boolean> updateOrderPaid(@PathVariable String orderNo) {
        return Result.success(orderService.markPaid(orderNo));
    }

    @Operation(summary = "标记已取消")
    @PostMapping("/{orderNo}/cancel")
    public Result<Boolean> updateOrderCancelled(@PathVariable String orderNo) {
        return Result.success(orderService.markCancelled(orderNo));
    }
}
