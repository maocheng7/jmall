package com.jmall.user.controller;

import com.jmall.common.core.constant.CommonConstants;
import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.Result;
import com.jmall.common.security.context.UserContextHolder;
import com.jmall.user.entity.User;
import com.jmall.user.entity.UserAddress;
import com.jmall.user.entity.UserFavorite;
import com.jmall.user.entity.UserFootprint;
import com.jmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器（用户端接口，需登录）
 * <p>
 * 提供个人信息、收货地址、收藏、足迹等功能。
 * userId 通过网关传递的请求头 X-User-Id 获取。
 * </p>
 *
 * @author jmall
 */
@Tag(name = "用户中心", description = "个人信息/收货地址/收藏/足迹")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==================== 个人信息 ====================

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = UserContextHolder.getUserId();
        return Result.success(userService.getById(userId));
    }

    @Operation(summary = "更新个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String avatar,
            @RequestParam(required = false) Integer gender) {
        userService.updateProfile(UserContextHolder.getUserId(), nickname, avatar, gender);
        return Result.success();
    }

    // ==================== 收货地址 ====================

    @Operation(summary = "收货地址列表")
    @GetMapping("/address")
    public Result<List<UserAddress>> listAddress() {
        return Result.success(userService.listAddress(UserContextHolder.getUserId()));
    }

    @Operation(summary = "新增收货地址")
    @PostMapping("/address")
    public Result<UserAddress> addAddress(@RequestBody UserAddress address) {
        return Result.success(userService.addAddress(UserContextHolder.getUserId(), address));
    }

    @Operation(summary = "更新收货地址")
    @PutMapping("/address/{addressId}")
    public Result<Void> updateAddress(@PathVariable Long addressId, @RequestBody UserAddress address) {
        userService.updateAddress(UserContextHolder.getUserId(), addressId, address);
        return Result.success();
    }

    @Operation(summary = "删除收货地址")
    @DeleteMapping("/address/{addressId}")
    public Result<Void> deleteAddress(@PathVariable Long addressId) {
        userService.deleteAddress(UserContextHolder.getUserId(), addressId);
        return Result.success();
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/address/default")
    public Result<UserAddress> getDefaultAddress() {
        return Result.success(userService.getDefaultAddress(UserContextHolder.getUserId()));
    }

    // ==================== 收藏 ====================

    @Operation(summary = "添加收藏")
    @PostMapping("/favorite/{spuId}")
    public Result<Void> addFavorite(@PathVariable Long spuId) {
        userService.addFavorite(UserContextHolder.getUserId(), spuId);
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorite/{spuId}")
    public Result<Void> removeFavorite(@PathVariable Long spuId) {
        userService.removeFavorite(UserContextHolder.getUserId(), spuId);
        return Result.success();
    }

    @Operation(summary = "收藏列表")
    @GetMapping("/favorite")
    public Result<PageResult<UserFavorite>> listFavorite(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.listFavorite(UserContextHolder.getUserId(), page, size));
    }

    @Operation(summary = "是否已收藏")
    @GetMapping("/favorite/{spuId}/check")
    public Result<Boolean> isFavorite(@PathVariable Long spuId) {
        return Result.success(userService.isFavorite(UserContextHolder.getUserId(), spuId));
    }

    // ==================== 足迹 ====================

    @Operation(summary = "记录浏览足迹")
    @PostMapping("/footprint")
    public Result<Void> addFootprint(
            @RequestParam Long spuId,
            @RequestParam(required = false) Long skuId) {
        userService.addFootprint(UserContextHolder.getUserId(), spuId, skuId);
        return Result.success();
    }

    @Operation(summary = "浏览足迹列表")
    @GetMapping("/footprint")
    public Result<PageResult<UserFootprint>> listFootprint(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.listFootprint(UserContextHolder.getUserId(), page, size));
    }
}
