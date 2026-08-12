package com.jmall.coupon.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; import com.jmall.coupon.entity.Promotion; import com.jmall.coupon.mapper.PromotionMapper; import com.jmall.coupon.service.PromotionService; import org.springframework.stereotype.Service;
@Service public class PromotionServiceImpl extends ServiceImpl<PromotionMapper,Promotion> implements PromotionService {}
