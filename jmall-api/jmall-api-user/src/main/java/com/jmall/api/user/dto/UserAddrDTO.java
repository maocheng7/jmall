package com.jmall.api.user.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 收货地址 DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class UserAddrDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 地址ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 收货人手机号
     */
    private String receiverPhone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String district;

    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 是否默认地址（0=否, 1=是）
     */
    private Integer isDefault;
}
