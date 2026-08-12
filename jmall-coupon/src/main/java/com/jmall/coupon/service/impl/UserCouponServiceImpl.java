package com.jmall.coupon.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; import com.jmall.coupon.entity.UserCoupon; import com.jmall.coupon.mapper.UserCouponMapper; import com.jmall.coupon.service.UserCouponService; import org.springframework.stereotype.Service;
@Service public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper,UserCoupon> implements UserCouponService {}
