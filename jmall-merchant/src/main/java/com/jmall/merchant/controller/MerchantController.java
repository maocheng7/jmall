package com.jmall.merchant.controller;

import com.jmall.api.merchant.dto.ShopDTO;
import com.jmall.common.core.result.Result;
import com.jmall.common.security.context.UserContextHolder;
import com.jmall.merchant.entity.MerchantApply;
import com.jmall.merchant.entity.Shop;
import com.jmall.merchant.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequiredArgsConstructor @RequestMapping("/api/merchant")
public class MerchantController {
 private final MerchantService service;
 @GetMapping("/inner/{merchantId}") public Result<ShopDTO> getByMerchant(@PathVariable Long merchantId){return Result.success(service.getShopByMerchantId(merchantId));}
 @GetMapping("/inner/shop/{shopId}") public Result<ShopDTO> getByShop(@PathVariable Long shopId){return Result.success(service.getShopById(shopId));}
 @GetMapping("/inner/{merchantId}/approved") public Result<Boolean> approved(@PathVariable Long merchantId){return Result.success(service.isApproved(merchantId));}
 @PostMapping("/apply") public Result<MerchantApply> apply(@RequestBody MerchantApply a){if(a.getUserId()==null)a.setUserId(UserContextHolder.getUserId());return Result.success(service.apply(a));}
 @GetMapping("/apply/{userId}") public Result<List<MerchantApply>> applies(@PathVariable Long userId){return Result.success(service.listApply(userId));}
 @PostMapping("/shop") public Result<Shop> createShop(@RequestBody Shop s){return Result.success(service.saveShop(s));}
 @PutMapping("/shop/{shopId}") public Result<Shop> updateShop(@PathVariable Long shopId,@RequestBody Shop s){return Result.success(service.updateShop(shopId,s));}
}
