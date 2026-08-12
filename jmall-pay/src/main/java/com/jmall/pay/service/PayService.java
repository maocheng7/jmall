package com.jmall.pay.service;

import com.jmall.api.pay.dto.PayResultDTO;

import java.math.BigDecimal;

/**
 * 支付服务
 *
 * @author jmall
 */
public interface PayService {

    /** 创建支付单（模拟支付可直接成功） */
    PayResultDTO createPayment(String orderNo, Integer payType, BigDecimal amount);

    /** 查询支付结果 */
    PayResultDTO getPayResult(String orderNo);

    /** 退款 */
    boolean refund(String orderNo);

    /** 用户端主动发起支付（会回调订单） */
    PayResultDTO payOrder(Long userId, String orderNo, Integer payType);
}
