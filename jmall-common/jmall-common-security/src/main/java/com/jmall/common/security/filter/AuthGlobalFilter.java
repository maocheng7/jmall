package com.jmall.common.security.filter;

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
 *
 * @author jmall
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 白名单路径（无需鉴权）
     */
    private static final List<String> WHITE_LIST = List.of(
            "/auth/register",
            "/auth/login",
            "/auth/login/sms",
            "/auth/login/wechat",
            "/auth/code/send",
            "/auth/check",
            "/product/list",
            "/product/detail/**",
            "/product/category/**",
            "/search/**",
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
        String method = request.getMethod().name();

        // 白名单放行
        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        // Sa-Token 校验登录状态
        try {
            if (!StpUtil.isLogin()) {
                return unauthorizedResponse(exchange, ResultCode.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("鉴权异常: path={}, error={}", path, e.getMessage());
            return unauthorizedResponse(exchange, ResultCode.UNAUTHORIZED);
        }

        // 获取登录用户信息，通过请求头传递给下游服务
        Long userId = StpUtil.getLoginIdAsLong();
        String username = (String) StpUtil.getLoginId().toString();

        // 从 Token Session 获取角色信息
        String role = (String) StpUtil.getTokenSession().get("role");
        Long merchantId = (Long) StpUtil.getTokenSession().get("merchantId");

        // 将用户信息写入请求头，传递给下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(CommonConstants.HEADER_USER_ID, String.valueOf(userId))
                .header(CommonConstants.HEADER_USERNAME, username != null ? username : "")
                .header(CommonConstants.HEADER_USER_ROLE, role != null ? role : "user")
                .header(CommonConstants.HEADER_MERCHANT_ID, merchantId != null ? String.valueOf(merchantId) : "")
                .header(CommonConstants.HEADER_SOURCE, CommonConstants.SOURCE_INTERNAL)
                .build();

        log.debug("鉴权通过: path={}, method={}, userId={}", path, method, userId);
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
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
