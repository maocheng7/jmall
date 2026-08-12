package com.jmall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 商品服务启动类
 * <p>
 * 职责：商品分类 / 商品 CRUD / SKU 规格管理 / ES 搜索索引同步
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallProductApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Product Started ✓        ║
                ║          商品服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}