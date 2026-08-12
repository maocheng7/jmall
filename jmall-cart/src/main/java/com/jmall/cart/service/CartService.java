package com.jmall.cart.service;

import com.jmall.cart.dto.CartAddDTO;
import com.jmall.cart.model.CartItem;

import java.util.List;

/**
 * 购物车服务
 *
 * @author jmall
 */
public interface CartService {

    void add(Long userId, CartAddDTO dto);

    void updateQuantity(Long userId, Long skuId, Integer quantity);

    void remove(Long userId, Long skuId);

    void removeBatch(Long userId, List<Long> skuIds);

    void check(Long userId, Long skuId, Boolean checked);

    void checkAll(Long userId, Boolean checked);

    List<CartItem> list(Long userId);

    void clear(Long userId);

    /** 结算：返回已勾选的购物车项 */
    List<CartItem> checkout(Long userId);
}
