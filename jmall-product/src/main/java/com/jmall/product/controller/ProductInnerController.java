package com.jmall.product.controller;

import com.jmall.api.product.dto.SkuDTO;
import com.jmall.common.core.result.Result;
import com.jmall.product.entity.ProductSku;
import com.jmall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品内部接口（供 cart/order/search 等服务 Feign 调用）
 *
 * @author jmall
 */
@Tag(name = "商品内部接口")
@RestController
@RequestMapping("/api/product/inner")
@RequiredArgsConstructor
public class ProductInnerController {

    private final ProductService productService;

    @Operation(summary = "根据SKU ID查询")
    @GetMapping("/sku/{skuId}")
    public Result<SkuDTO> getSkuById(@PathVariable("skuId") Long skuId) {
        ProductSku sku = productService.getSkuById(skuId);
        return Result.success(productService.toSkuDTO(sku));
    }

    @Operation(summary = "批量查询SKU")
    @PostMapping("/sku/list")
    public Result<List<SkuDTO>> listSkuByIds(@RequestBody List<Long> skuIds) {
        List<SkuDTO> list = productService.listSkuByIds(skuIds).stream()
                .map(productService::toSkuDTO)
                .collect(Collectors.toList());
        return Result.success(list);
    }

    @Operation(summary = "校验SKU是否上架可售")
    @GetMapping("/sku/{skuId}/status")
    public Result<Boolean> checkSkuOnSale(@PathVariable("skuId") Long skuId) {
        return Result.success(productService.checkSkuOnSale(skuId));
    }
}
