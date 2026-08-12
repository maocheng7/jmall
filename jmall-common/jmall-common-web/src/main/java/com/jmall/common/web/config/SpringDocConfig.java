package com.jmall.common.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 接口文档配置
 * <p>
 * 自动生成各服务的 API 文档，访问地址：{@code /swagger-ui.html}
 * </p>
 *
 * @author jmall
 */
@Configuration
public class SpringDocConfig {

    @Value("${spring.application.name:jmall-service}")
    private String applicationName;

    @Value("${springdoc.info.title:J-Mall API文档}")
    private String title;

    @Value("${springdoc.info.description:J-Mall仿京东商城系统接口文档}")
    private String description;

    @Value("${springdoc.info.version:1.0.0}")
    private String version;

    /**
     * API 文档基础信息
     */
    @Bean
    public OpenAPI jmallOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title + " - " + applicationName)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name("J-Mall Team")
                                .email("dev@jmall.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
