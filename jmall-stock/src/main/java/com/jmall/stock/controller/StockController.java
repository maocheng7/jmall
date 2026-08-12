package com.jmall.stock.controller;

import com.jmall.common.core.result.Result;
import com.jmall.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理接口（商家/运营）
 *
 * @author jmall
 */
@Tag(name = "库存管理")
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "设置库存")
    @PostMapping("/set")
    public Result<Void> setStock(@RequestParam Long skuId,
                                 @RequestParam(required = false) Long merchantId,
                                 @RequestParam Integer quantity) {
        stockService.setStock(skuId, merchantId, quantity);
        return Result.success();
    }

    @Operation(summary = "查询可用库存")
    @GetMapping("/{skuId}")
    public Result<Integer> get(@PathVariable Long skuId) {
        return Result.success(stockService.getAvailable(skuId));
    }
}
