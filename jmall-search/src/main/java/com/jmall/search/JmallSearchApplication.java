package com.jmall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 搜索服务启动类
 * <p>
 * 职责：ES 商品搜索 / 筛选 / 排序，消费商品上下架消息维护索引
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallSearchApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Search Started ✓         ║
                ║          搜索服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}