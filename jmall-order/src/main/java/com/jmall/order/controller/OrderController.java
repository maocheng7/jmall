package com.jmall.order.controller;

import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.Result;
import com.jmall.common.security.context.UserContextHolder;
import com.jmall.order.dto.OrderCreateDTO;
import com.jmall.order.entity.Order;
import com.jmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单用户端接口
 *
 * @author jmall
 */
@Tag(name = "订单")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.create(UserContextHolder.getUserId(), dto));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@PathVariable String orderNo,
                               @RequestParam(required = false, defaultValue = "用户取消") String reason) {
        orderService.cancel(UserContextHolder.getUserId(), orderNo, reason);
        return Result.success();
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderNo}")
    public Result<Order> detail(@PathVariable String orderNo) {
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUserId().equals(UserContextHolder.getUserId())) {
            return Result.fail(com.jmall.common.core.result.ResultCode.FORBIDDEN);
        }
        return Result.success(order);
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/list")
    public Result<PageResult<Order>> list(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(orderService.pageByUser(UserContextHolder.getUserId(), status, page, size));
    }
}
