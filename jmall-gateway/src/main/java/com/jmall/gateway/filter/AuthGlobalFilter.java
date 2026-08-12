package com.jmall.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.jmall.common.core.constant.CommonConstants;
import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 网关全局鉴权过滤器
 * <p>
 * 在网关层统一校验 Sa-Token 登录状态，并将用户信息通过请求头传递给下游服务。
 * 白名单路径（注册、登录、公开接口）直接放行。
 * </p>
 * <p>
 * 注意：Sa-Token 的 StpUtil 是同步阻塞 API，在 WebFlux 响应式环境中
 * 必须用 {@link Mono#fromCallable} 包装，避免阻塞响应式线程。
 * </p>
 *
 * @author jmall
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 白名单路径（无需鉴权）
     * <p>
     * 路径匹配网关入口的完整路径（StripPrefix 之前），如：
     * - /auth/** → 认证服务（StripPrefix=0，路径不变）
     * - /api/product/list → 商品服务（StripPrefix=1 后变为 /product/list）
     * 白名单使用网关入口路径进行匹配。
     * </p>
     */
    private static final List<String> WHITE_LIST = List.of(
            // 认证服务（StripPrefix=0，路径不变）
            "/auth/register",
            "/auth/login",
            "/auth/login/sms",
            "/auth/login/wechat",
            "/auth/code/send",
            "/auth/check",
            // 商品公开接口（网关路径 /api/product/**，StripPrefix=1 后 /product/**）
            "/api/product/list",
            "/api/product/detail/**",
            "/api/product/category/**",
            // 搜索公开接口
            "/api/search/**",
            // 监控/文档
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod() != null ? request.getMethod().name() : "UNKNOWN";

        // 白名单放行
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        // Sa-Token 校验登录状态 —— 用 Mono.fromCallable 包装同步调用，避免阻塞响应式线程
        return Mono.fromCallable(StpUtil::isLogin)
                .flatMap(isLogin -> {
                    if (!isLogin) {
                        return unauthorizedResponse(exchange, ResultCode.UNAUTHORIZED);
                    }
                    // 获取登录用户信息并写入请求头传递给下游服务
                    return Mono.fromCallable(() -> {
                        Long userId = StpUtil.getLoginIdAsLong();
                        String username = StpUtil.getLoginId().toString();
                        String role = (String) StpUtil.getTokenSession().get("role");
                        Long merchantId = (Long) StpUtil.getTokenSession().get("merchantId");

                        ServerHttpRequest mutatedRequest = request.mutate()
                                .header(CommonConstants.HEADER_USER_ID, String.valueOf(userId))
                                .header(CommonConstants.HEADER_USERNAME, username != null ? username : "")
                                .header(CommonConstants.HEADER_USER_ROLE, role != null ? role : "user")
                                .header(CommonConstants.HEADER_MERCHANT_ID, merchantId != null ? String.valueOf(merchantId) : "")
                                .header(CommonConstants.HEADER_SOURCE, CommonConstants.SOURCE_INTERNAL)
                                .build();

                        log.debug("鉴权通过: path={}, method={}, userId={}", path, method, userId);
                        return mutatedRequest;
                    }).flatMap(mutatedRequest ->
                            chain.filter(exchange.mutate().request(mutatedRequest).build()));
                })
                .onErrorResume(e -> {
                    log.error("鉴权异常: path={}, error={}", path, e.getMessage());
                    return unauthorizedResponse(exchange, ResultCode.UNAUTHORIZED);
                });
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, ResultCode resultCode) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<Void> result = Result.fail(resultCode);
        String json = JsonUtils.toJson(result);
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 过滤器优先级（值越小优先级越高）
     */
    @Override
    public int getOrder() {
        return -100;
    }
}
