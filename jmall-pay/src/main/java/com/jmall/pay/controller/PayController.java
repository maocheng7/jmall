package com.jmall.pay.controller;

import com.jmall.api.pay.dto.PayResultDTO;
import com.jmall.common.core.result.Result;
import com.jmall.common.security.context.UserContextHolder;
import com.jmall.pay.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付用户端接口
 *
 * @author jmall
 */
@Tag(name = "支付")
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    @Operation(summary = "发起支付（模拟）")
    @PostMapping("/{orderNo}")
    public Result<PayResultDTO> pay(@PathVariable String orderNo,
                                    @RequestParam(defaultValue = "4") Integer payType) {
        return Result.success(payService.payOrder(UserContextHolder.getUserId(), orderNo, payType));
    }

    @Operation(summary = "查询支付结果")
    @GetMapping("/{orderNo}")
    public Result<PayResultDTO> result(@PathVariable String orderNo) {
        return Result.success(payService.getPayResult(orderNo));
    }
}
