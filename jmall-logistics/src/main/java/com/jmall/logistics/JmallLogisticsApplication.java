package com.jmall.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 物流服务启动类
 * <p>
 * 职责：运单创建 / 快递轨迹查询（对接快递鸟/顺丰）/ 物流状态回调
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallLogisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallLogisticsApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Logistics Started ✓      ║
                ║          物流服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}