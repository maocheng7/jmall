package com.jmall.common.core.result;

import lombok.Getter;

/**
 * 系统统一响应状态码
 * <p>
 * 编码规则：
 * - 成功：200
 * - 通用错误：4xx（客户端错误）、5xx（服务端错误）
 * - 业务错误：10xxx 起，按模块分段
 * </p>
 *
 * @author jmall
 */
@Getter
public enum ResultCode {

    /* ==================== 成功 ==================== */
    SUCCESS(200, "操作成功"),

    /* ==================== 通用错误 4xx ==================== */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    REQUEST_TIMEOUT(408, "请求超时"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    /* ==================== 通用错误 5xx ==================== */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    REMOTE_CALL_ERROR(505, "远程服务调用失败"),

    /* ==================== 认证模块 10001-10999 ==================== */
    AUTH_ACCOUNT_NOT_FOUND(10001, "账号不存在"),
    AUTH_PASSWORD_ERROR(10002, "密码错误"),
    AUTH_ACCOUNT_EXISTS(10003, "账号已存在"),
    AUTH_PHONE_EXISTS(10004, "手机号已注册"),
    AUTH_CODE_ERROR(10005, "验证码错误或已过期"),
    AUTH_CODE_SEND_TOO_FREQUENT(10006, "验证码发送过于频繁"),
    AUTH_TOKEN_INVALID(10007, "Token无效"),
    AUTH_TOKEN_EXPIRED(10008, "Token已过期"),
    AUTH_ACCOUNT_DISABLED(10009, "账号已被禁用"),
    AUTH_WX_LOGIN_ERROR(10010, "微信登录失败"),

    /* ==================== 用户模块 11001-11999 ==================== */
    USER_NOT_FOUND(11001, "用户不存在"),
    USER_ADDRESS_LIMIT(11002, "收货地址数量已达上限"),
    USER_FAVORITE_EXISTS(11003, "已收藏该商品"),

    /* ==================== 商品模块 12001-12999 ==================== */
    PRODUCT_NOT_FOUND(12001, "商品不存在"),
    PRODUCT_OFF_SHELF(12002, "商品已下架"),
    SKU_NOT_FOUND(12003, "商品规格不存在"),
    CATEGORY_NOT_FOUND(12004, "分类不存在"),
    CATEGORY_HAS_CHILDREN(12005, "该分类下有子分类，无法删除"),
    PRODUCT_SHELF_ERROR(12006, "商品上下架失败"),

    /* ==================== 购物车模块 13001-13999 ==================== */
    CART_ITEM_NOT_FOUND(13001, "购物车项不存在"),
    CART_SKU_EXISTS(13002, "该商品已在购物车中"),

    /* ==================== 订单模块 14001-14999 ==================== */
    ORDER_NOT_FOUND(14001, "订单不存在"),
    ORDER_STATUS_ERROR(14002, "订单状态不允许此操作"),
    ORDER_STOCK_NOT_ENOUGH(14003, "商品库存不足"),
    ORDER_CREATE_ERROR(14004, "订单创建失败"),
    ORDER_CANCEL_ERROR(14005, "订单取消失败"),
    ORDER_PAY_TIMEOUT(14006, "订单已超时，请重新下单"),
    ORDER_REFUND_ERROR(14007, "退款申请失败"),

    /* ==================== 支付模块 15001-15999 ==================== */
    PAY_ERROR(15001, "支付失败"),
    PAY_CALLBACK_ERROR(15002, "支付回调处理失败"),
    PAY_REFUND_ERROR(15003, "退款失败"),

    /* ==================== 库存模块 16001-16999 ==================== */
    STOCK_NOT_ENOUGH(16001, "库存不足"),
    STOCK_LOCK_FAIL(16002, "库存锁定失败"),
    STOCK_DEDUCT_FAIL(16003, "库存扣减失败"),

    /* ==================== 优惠模块 17001-17999 ==================== */
    COUPON_NOT_FOUND(17001, "优惠券不存在"),
    COUPON_EXPIRED(17002, "优惠券已过期"),
    COUPON_USED(17003, "优惠券已使用"),
    COUPON_NOT_AVAILABLE(17004, "优惠券不可用"),
    COUPON_NOT_ENOUGH(17005, "优惠券已领取完"),

    /* ==================== 秒杀模块 18001-18999 ==================== */
    SECKILL_NOT_START(18001, "秒杀活动未开始"),
    SECKILL_ENDED(18002, "秒杀活动已结束"),
    SECKILL_SOLD_OUT(18003, "秒杀商品已售罄"),
    SECKILL_REPEAT(18004, "不可重复参与秒杀"),

    /* ==================== 商家模块 19001-19999 ==================== */
    MERCHANT_NOT_FOUND(19001, "商家不存在"),
    MERCHANT_NOT_APPROVED(19002, "商家未通过审核"),
    MERCHANT_SHOP_EXISTS(19003, "店铺已存在"),
    MERCHANT_APPLY_PENDING(19004, "入驻申请审核中"),

    /* ==================== 物流模块 20001-20999 ==================== */
    LOGISTICS_NOT_FOUND(20001, "物流信息不存在"),
    LOGISTICS_QUERY_ERROR(20002, "物流查询失败"),

    /* ==================== 文件模块 21001-21999 ==================== */
    FILE_UPLOAD_ERROR(21001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORT(21002, "文件类型不支持"),
    FILE_SIZE_EXCEED(21003, "文件大小超出限制");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 提示信息
     */
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
