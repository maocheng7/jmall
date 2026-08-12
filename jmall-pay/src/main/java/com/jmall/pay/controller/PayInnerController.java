package com.jmall.pay.controller;

import com.jmall.api.pay.dto.PayResultDTO;
import com.jmall.common.core.result.Result;
import com.jmall.pay.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付内部接口（对齐 RemotePayService）
 *
 * @author jmall
 */
@Tag(name = "支付内部接口")
@RestController
@RequestMapping("/api/pay/inner")
@RequiredArgsConstructor
public class PayInnerController {

    private final PayService payService;

    @Operation(summary = "创建支付")
    @PostMapping("/create")
    public Result<PayResultDTO> createPayment(@RequestParam String orderNo,
                                              @RequestParam Integer payType,
                                              @RequestParam BigDecimal amount) {
        return Result.success(payService.createPayment(orderNo, payType, amount));
    }

    @Operation(summary = "查询支付结果")
    @GetMapping("/{orderNo}")
    public Result<PayResultDTO> getPayResult(@PathVariable String orderNo) {
        return Result.success(payService.getPayResult(orderNo));
    }

    @Operation(summary = "退款")
    @PostMapping("/{orderNo}/refund")
    public Result<Boolean> refund(@PathVariable String orderNo) {
        return Result.success(payService.refund(orderNo));
    }
}
