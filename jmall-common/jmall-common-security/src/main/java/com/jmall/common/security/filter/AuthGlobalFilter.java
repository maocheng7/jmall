package com.jmall.common.security.filter;

/**
 * 此文件已移至 jmall-gateway 模块：
 * com.jmall.gateway.filter.AuthGlobalFilter
 *
 * 原因：AuthGlobalFilter 依赖 spring-cloud-gateway（WebFlux reactive），
 * 不应放在被 MVC 服务共用的 common-security 模块中，否则会导致
 * MVC 服务类路径上同时存在 WebFlux 和 Spring MVC 而冲突。
 *
 * @author jmall
 */
final class AuthGlobalFilterMoved {
    private AuthGlobalFilterMoved() {}
}
