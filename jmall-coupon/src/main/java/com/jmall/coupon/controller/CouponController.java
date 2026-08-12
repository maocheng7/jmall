package com.jmall.coupon.controller;
import com.jmall.common.core.result.Result; import com.jmall.common.security.context.UserContextHolder; import com.jmall.coupon.entity.*; import com.jmall.coupon.service.CouponService; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.math.BigDecimal; import java.util.List;
@RestController @RequestMapping("/api/coupon/inner") @RequiredArgsConstructor
public class CouponController { private final CouponService service;
 @PostMapping("/create") public Result<Coupon> create(@RequestBody Coupon c){if(c.getStatus()==null)c.setStatus(1);service.save(c);return Result.success(c);}
 @PostMapping("/{couponId}/receive") public Result<UserCoupon> receive(@PathVariable Long couponId,@RequestParam(required=false) Long userId){return Result.success(service.receive(couponId,userId!=null?userId:UserContextHolder.getUserId()));}
 @GetMapping("/user/{userId}") public Result<List<UserCoupon>> list(@PathVariable Long userId){return Result.success(service.userCoupons(userId));}
 @PostMapping("/{userCouponId}/use") public Result<Void> use(@PathVariable Long userCouponId,@RequestParam String orderNo){service.use(userCouponId,orderNo);return Result.success();}
 @PostMapping("/{userCouponId}/refund") public Result<Void> refund(@PathVariable Long userCouponId){service.refund(userCouponId);return Result.success();}
 @GetMapping("/{userCouponId}") public Result<UserCoupon> get(@PathVariable Long userCouponId){return Result.success(service.getUserCoupon(userCouponId));}
 @GetMapping("/{userCouponId}/calculate") public Result<BigDecimal> calculate(@PathVariable Long userCouponId,@RequestParam BigDecimal orderAmount){return Result.success(service.calculate(userCouponId,orderAmount));}
 @PostMapping("/receive/{couponId}") public Result<UserCoupon> userReceive(@PathVariable Long couponId){return receive(couponId,UserContextHolder.getUserId());}
 @GetMapping("/mine") public Result<List<UserCoupon>> mine(){return Result.success(service.userCoupons(UserContextHolder.getUserId()));}
}
