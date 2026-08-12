package com.jmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * J-Mall 优惠服务启动类
 * <p>
 * 职责：优惠券管理/发放/核销、满减活动、促销活动、秒杀活动（Redis+RocketMQ）
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
public class JmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallCouponApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Coupon Started ✓         ║
                ║          优惠服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}