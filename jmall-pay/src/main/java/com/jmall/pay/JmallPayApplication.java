package com.jmall.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 支付服务启动类
 * <p>
 * 职责：微信支付 / 支付宝支付 / 模拟支付 / 支付回调 / 退款
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallPayApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallPayApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Pay Started ✓            ║
                ║          支付服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}