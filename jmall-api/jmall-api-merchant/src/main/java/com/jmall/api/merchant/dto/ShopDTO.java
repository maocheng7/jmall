package com.jmall.api.merchant.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 店铺信息 DTO（服务间调用传输对象）
 *
 * @author jmall
 */
@Data
public class ShopDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺Logo
     */
    private String logo;

    /**
     * 店铺描述
     */
    private String description;

    /**
     * 状态（0=关闭, 1=正常）
     */
    private Integer status;
}
