package com.jmall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 购物车服务启动类
 * <p>
 * 职责：购物车增删改查 / 批量结算（基于 Redis Hash 存储）
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallCartApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Cart Started ✓           ║
                ║          购物车服务启动成功           ║
                ╚══════════════════════════════════════╝
                """);
    }
}