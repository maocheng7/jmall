package com.jmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * J-Mall API 网关启动类
 * <p>
 * 职责：
 * 1. 路由转发 - 将请求路由到对应的微服务
 * 2. 统一鉴权 - Sa-Token 校验登录状态，传递用户信息给下游
 * 3. 限流熔断 - Sentinel 限流保护
 * 4. 跨域处理 - 统一 CORS 配置
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.jmall.gateway", "com.jmall.common"})
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Gateway Started ✓       ║
                ║          网关服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}
