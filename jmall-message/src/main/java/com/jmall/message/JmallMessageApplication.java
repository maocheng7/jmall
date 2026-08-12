package com.jmall.message;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * J-Mall 消息服务启动类
 * <p>
 * 职责：短信发送（阿里云/腾讯云）/ 站内信 / APP推送通知 / 消息记录
 * </p>
 *
 * @author jmall
 */
@SpringBootApplication
@EnableDiscoveryClient
public class JmallMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmallMessageApplication.class, args);
        System.out.println("""
                ╔══════════════════════════════════════╗
                ║       J-Mall Message Started ✓        ║
                ║          消息服务启动成功             ║
                ╚══════════════════════════════════════╝
                """);
    }
}