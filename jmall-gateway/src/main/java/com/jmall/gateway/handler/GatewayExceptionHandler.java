package com.jmall.gateway.handler;

import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局异常处理
 * <p>
 * 拦截网关层异常（如服务未找到、路由失败等），统一返回标准 {@link Result} 格式。
 * </p>
 *
 * @author jmall
 */
@Slf4j
@Order(-1)
@Configuration
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<Void> result;

        if (ex instanceof NotFoundException) {
            log.warn("服务未找到: {}", ex.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND);
            result = Result.fail(ResultCode.SERVICE_UNAVAILABLE.getCode(), "服务不可用: " + exchange.getRequest().getURI().getPath());
        } else {
            log.error("网关异常: {}", ex.getMessage(), ex);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            result = Result.fail(ResultCode.GATEWAY_TIMEOUT.getCode(), "网关处理异常，请稍后再试");
        }

        String json = JsonUtils.toJson(result);
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
