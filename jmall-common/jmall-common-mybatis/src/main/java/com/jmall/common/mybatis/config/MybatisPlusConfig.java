package com.jmall.common.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 * <p>
 * 配置分页插件和乐观锁插件。
 * </p>
 *
 * @author jmall
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * MyBatis-Plus 拦截器配置
     * <p>
     * 包含：
     * 1. 分页插件（PaginationInnerInterceptor）- 支持分页查询
     * 2. 乐观锁插件（OptimisticLockerInnerInterceptor）- 支持乐观锁更新
     * </p>
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 乐观锁插件（需在分页插件之前）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 分页插件（MySQL 类型）
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 单页最大记录数限制
        pageInterceptor.setMaxLimit(500L);
        // 溢出总页数后是否返回首页数据
        pageInterceptor.setOverflow(false);
        interceptor.addInnerInterceptor(pageInterceptor);
        return interceptor;
    }
}
