package com.jmall.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 商家服务启动类
 * <p>
 * 职责：商家入驻申请/审核 / 店铺管理 / 商家商品管理 / 商家订单管理 / 数据统计
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallMerchantApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallMerchantApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Merchant Started ✓       ║
                ║          商家服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}