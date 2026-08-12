package com.jmall.coupon.service;
import com.baomidou.mybatisplus.extension.service.IService; import com.jmall.coupon.entity.Coupon; import com.jmall.coupon.entity.UserCoupon; import java.math.BigDecimal; import java.util.List;
public interface CouponService extends IService<Coupon> { UserCoupon receive(Long couponId,Long userId); List<UserCoupon> userCoupons(Long userId); UserCoupon getUserCoupon(Long id); void use(Long id,String orderNo); void refund(Long id); BigDecimal calculate(Long id,BigDecimal orderAmount); }
