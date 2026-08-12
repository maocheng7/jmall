package com.jmall.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式枚举
 *
 * @author jmall
 */
@Getter
@AllArgsConstructor
public enum PayTypeEnum {

    WECHAT(1, "微信支付"),
    ALIPAY(2, "支付宝支付"),
    BALANCE(3, "余额支付"),
    MOCK(4, "模拟支付");

    private final int code;
    private final String desc;
}
