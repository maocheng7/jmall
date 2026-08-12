package com.jmall.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 库存服务启动类
 * <p>
 * 职责：库存预占 / 扣减 / 回滚 / 库存查询（Redis 缓存 + MQ 异步落库）
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallStockApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallStockApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Stock Started ✓          ║
                ║          库存服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}