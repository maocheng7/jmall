package com.jmall.order.service;

import com.jmall.api.order.dto.OrderDTO;
import com.jmall.common.core.result.PageResult;
import com.jmall.order.dto.OrderCreateDTO;
import com.jmall.order.entity.Order;

/**
 * 订单服务
 *
 * @author jmall
 */
public interface OrderService {

    /** 创建订单（扣库存） */
    Order create(Long userId, OrderCreateDTO dto);

    /** 取消订单（回滚库存） */
    void cancel(Long userId, String orderNo, String reason);

    /** 标记已支付 */
    boolean markPaid(String orderNo);

    /** 标记取消（内部） */
    boolean markCancelled(String orderNo);

    Order getByOrderNo(String orderNo);

    PageResult<Order> pageByUser(Long userId, Integer status, int page, int size);

    OrderDTO toDTO(Order order);
}
