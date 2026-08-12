package com.jmall.cart.controller;

import com.jmall.cart.dto.CartAddDTO;
import com.jmall.cart.model.CartItem;
import com.jmall.cart.service.CartService;
import com.jmall.common.core.result.Result;
import com.jmall.common.security.context.UserContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 *
 * @author jmall
 */
@Tag(name = "购物车")
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "加入购物车")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody CartAddDTO dto) {
        cartService.add(UserContextHolder.getUserId(), dto);
        return Result.success();
    }

    @Operation(summary = "购物车列表")
    @GetMapping
    public Result<List<CartItem>> list() {
        return Result.success(cartService.list(UserContextHolder.getUserId()));
    }

    @Operation(summary = "修改数量")
    @PutMapping("/{skuId}")
    public Result<Void> updateQuantity(@PathVariable Long skuId, @RequestParam Integer quantity) {
        cartService.updateQuantity(UserContextHolder.getUserId(), skuId, quantity);
        return Result.success();
    }

    @Operation(summary = "删除单项")
    @DeleteMapping("/{skuId}")
    public Result<Void> remove(@PathVariable Long skuId) {
        cartService.remove(UserContextHolder.getUserId(), skuId);
        return Result.success();
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    public Result<Void> removeBatch(@RequestBody List<Long> skuIds) {
        cartService.removeBatch(UserContextHolder.getUserId(), skuIds);
        return Result.success();
    }

    @Operation(summary = "勾选/取消勾选")
    @PutMapping("/{skuId}/check")
    public Result<Void> check(@PathVariable Long skuId, @RequestParam Boolean checked) {
        cartService.check(UserContextHolder.getUserId(), skuId, checked);
        return Result.success();
    }

    @Operation(summary = "全选/取消全选")
    @PutMapping("/check-all")
    public Result<Void> checkAll(@RequestParam Boolean checked) {
        cartService.checkAll(UserContextHolder.getUserId(), checked);
        return Result.success();
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public Result<Void> clear() {
        cartService.clear(UserContextHolder.getUserId());
        return Result.success();
    }

    @Operation(summary = "结算（返回已勾选商品）")
    @GetMapping("/checkout")
    public Result<List<CartItem>> checkout() {
        return Result.success(cartService.checkout(UserContextHolder.getUserId()));
    }
}
