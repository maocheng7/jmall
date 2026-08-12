package com.jmall.product.controller;

import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.Result;
import com.jmall.product.dto.SpuSaveDTO;
import com.jmall.product.entity.ProductSpu;
import com.jmall.product.service.ProductService;
import com.jmall.product.vo.ProductDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器（用户端 + 商家端）
 *
 * @author jmall
 */
@Tag(name = "商品管理")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品列表（用户端：仅上架）")
    @GetMapping("/list")
    public Result<PageResult<ProductSpu>> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productService.pageOnSale(categoryId, keyword, page, size));
    }

    @Operation(summary = "商品详情")
    @GetMapping("/detail/{spuId}")
    public Result<ProductDetailVO> detail(@PathVariable Long spuId) {
        return Result.success(productService.getDetail(spuId));
    }

    @Operation(summary = "发布/编辑商品（商家端）")
    @PostMapping("/spu")
    public Result<Long> saveSpu(@Valid @RequestBody SpuSaveDTO dto) {
        return Result.success(productService.saveSpu(dto));
    }

    @Operation(summary = "商家商品列表")
    @GetMapping("/merchant/{merchantId}")
    public Result<PageResult<ProductSpu>> merchantList(
            @PathVariable Long merchantId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productService.pageByMerchant(merchantId, status, page, size));
    }

    @Operation(summary = "上架")
    @PostMapping("/{spuId}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long spuId) {
        productService.onShelf(spuId);
        return Result.success();
    }

    @Operation(summary = "下架")
    @PostMapping("/{spuId}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long spuId) {
        productService.offShelf(spuId);
        return Result.success();
    }
}
