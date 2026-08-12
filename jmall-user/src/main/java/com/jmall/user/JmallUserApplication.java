package com.jmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * J-Mall 用户服务启动类
 * <p>
 * 职责：用户信息 CRUD / 收货地址管理 / 用户收藏
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.jmall.user", "com.jmall.common"})
@EnableDiscoveryClient
public class JmallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallUserApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall User Started ✓           ║
                ║          用户服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}