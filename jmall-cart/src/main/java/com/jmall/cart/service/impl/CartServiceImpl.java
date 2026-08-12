package com.jmall.cart.service.impl;

import com.jmall.api.product.dto.SkuDTO;
import com.jmall.api.product.feign.RemoteProductService;
import com.jmall.cart.dto.CartAddDTO;
import com.jmall.cart.model.CartItem;
import com.jmall.cart.service.CartService;
import com.jmall.common.core.constant.RedisConstants;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.redis.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 购物车实现（Redis Hash：key=jmall:cart:{userId}, field=skuId）
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CacheUtils cacheUtils;
    private final RemoteProductService remoteProductService;

    private String cartKey(Long userId) {
        return RedisConstants.CART_KEY + userId;
    }

    @Override
    public void add(Long userId, CartAddDTO dto) {
        Result<SkuDTO> skuResult = remoteProductService.getSkuById(dto.getSkuId());
        if (!skuResult.isSuccess() || skuResult.getData() == null) {
            throw new BusinessException(ResultCode.SKU_NOT_FOUND);
        }
        SkuDTO sku = skuResult.getData();
        if (sku.getStatus() == null || sku.getStatus() != 1) {
            throw new BusinessException(ResultCode.PRODUCT_OFF_SHELF);
        }

        String key = cartKey(userId);
        String field = String.valueOf(dto.getSkuId());
        Object exist = cacheUtils.hGet(key, field);
        CartItem item;
        if (exist instanceof CartItem cartItem) {
            item = cartItem;
            item.setQuantity(item.getQuantity() + dto.getQuantity());
        } else {
            item = new CartItem();
            item.setSkuId(sku.getSkuId());
            item.setSpuId(sku.getSpuId());
            item.setProductName(sku.getProductName());
            item.setSpecValue(sku.getSpecValue());
            item.setImageUrl(sku.getImageUrl());
            item.setPrice(sku.getPrice());
            item.setQuantity(dto.getQuantity());
            item.setChecked(true);
            item.setMerchantId(sku.getMerchantId());
        }
        cacheUtils.hSet(key, field, item);
        log.info("加购: userId={}, skuId={}, qty={}", userId, dto.getSkuId(), item.getQuantity());
    }

    @Override
    public void updateQuantity(Long userId, Long skuId, Integer quantity) {
        if (quantity == null || quantity < 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "数量至少为1");
        }
        CartItem item = getItem(userId, skuId);
        item.setQuantity(quantity);
        cacheUtils.hSet(cartKey(userId), String.valueOf(skuId), item);
    }

    @Override
    public void remove(Long userId, Long skuId) {
        cacheUtils.hDelete(cartKey(userId), String.valueOf(skuId));
    }

    @Override
    public void removeBatch(Long userId, List<Long> skuIds) {
        if (skuIds == null || skuIds.isEmpty()) {
            return;
        }
        Object[] fields = skuIds.stream().map(String::valueOf).toArray();
        cacheUtils.hDelete(cartKey(userId), fields);
    }

    @Override
    public void check(Long userId, Long skuId, Boolean checked) {
        CartItem item = getItem(userId, skuId);
        item.setChecked(Boolean.TRUE.equals(checked));
        cacheUtils.hSet(cartKey(userId), String.valueOf(skuId), item);
    }

    @Override
    public void checkAll(Long userId, Boolean checked) {
        for (CartItem item : list(userId)) {
            item.setChecked(Boolean.TRUE.equals(checked));
            cacheUtils.hSet(cartKey(userId), String.valueOf(item.getSkuId()), item);
        }
    }

    @Override
    public List<CartItem> list(Long userId) {
        Map<Object, Object> map = cacheUtils.hGetAll(cartKey(userId));
        List<CartItem> list = new ArrayList<>();
        if (map == null || map.isEmpty()) {
            return list;
        }
        for (Object value : map.values()) {
            if (value instanceof CartItem item) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public void clear(Long userId) {
        cacheUtils.delete(cartKey(userId));
    }

    @Override
    public List<CartItem> checkout(Long userId) {
        return list(userId).stream()
                .filter(i -> Boolean.TRUE.equals(i.getChecked()))
                .toList();
    }

    private CartItem getItem(Long userId, Long skuId) {
        Object value = cacheUtils.hGet(cartKey(userId), String.valueOf(skuId));
        if (!(value instanceof CartItem item)) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }
        return item;
    }
}
