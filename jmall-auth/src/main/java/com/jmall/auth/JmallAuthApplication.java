package com.jmall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * J-Mall 认证服务启动类
 * <p>
 * 职责：
 * 1. 用户注册（手机号/微信）
 * 2. 用户登录（密码/短信/微信）
 * 3. Sa-Token Token 签发与校验
 * 4. 验证码发送与验证
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.jmall.api")
public class JmallAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallAuthApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Auth Started ✓          ║
                ║          认证服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}