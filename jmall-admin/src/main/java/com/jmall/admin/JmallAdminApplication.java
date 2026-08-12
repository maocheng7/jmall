package com.jmall.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 平台管理服务启动类
 * <p>
 * 职责：用户管理 / 商家审核与管理 / 商品分类管理 / 首页内容管理 / 促销管理 / 订单监控 / 数据看板
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallAdminApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Admin Started ✓          ║
                ║          平台管理服务启动成功         ║
                ╚══════════════════════════════════════╝
                """);
    }
}