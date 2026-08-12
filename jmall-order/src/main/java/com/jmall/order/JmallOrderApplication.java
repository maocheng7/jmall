package com.jmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 订单服务启动类
 * <p>
 * 职责：下单（分布式事务）/ 支付对接 / 取消 / 退款 / 评价 / 订单查询
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallOrderApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Order Started ✓          ║
                ║          订单服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}