package com.jmall.stock.controller;

import com.jmall.api.stock.dto.StockDeductDTO;
import com.jmall.common.core.result.Result;
import com.jmall.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 库存内部接口（对齐 RemoteStockService）
 *
 * @author jmall
 */
@Tag(name = "库存内部接口")
@RestController
@RequestMapping("/api/stock/inner")
@RequiredArgsConstructor
public class StockInnerController {

    private final StockService stockService;

    @Operation(summary = "扣减库存")
    @PostMapping("/deduct")
    public Result<Boolean> deduct(@RequestBody StockDeductDTO dto) {
        return Result.success(stockService.deduct(dto));
    }

    @Operation(summary = "回滚库存")
    @PostMapping("/rollback/{orderNo}")
    public Result<Boolean> rollback(@PathVariable String orderNo) {
        return Result.success(stockService.rollback(orderNo));
    }

    @Operation(summary = "查询可用库存")
    @GetMapping("/{skuId}")
    public Result<Integer> getStock(@PathVariable Long skuId) {
        return Result.success(stockService.getAvailable(skuId));
    }
}
