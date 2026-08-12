package com.jmall.common.security.feign;

import com.jmall.common.core.constant.CommonConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

/**
 * Feign 请求拦截器
 * <p>
 * 在服务间 Feign 调用时透传 Token 和用户信息请求头。
 * 从 Feign 当前请求头中获取上游传递的值，不需要 Servlet 上下文。
 * </p>
 *
 * @author jmall
 */
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Map<String, Collection<String>> headers = template.headers();
        if (headers == null || headers.isEmpty()) {
            return;
        }

        copyHeader(headers, template, CommonConstants.HEADER_TOKEN);
        copyHeader(headers, template, CommonConstants.HEADER_USER_ID);
        copyHeader(headers, template, CommonConstants.HEADER_USERNAME);
        copyHeader(headers, template, CommonConstants.HEADER_USER_ROLE);
        copyHeader(headers, template, CommonConstants.HEADER_MERCHANT_ID);
        template.header(CommonConstants.HEADER_SOURCE, CommonConstants.SOURCE_INTERNAL);
    }

    private void copyHeader(Map<String, Collection<String>> headers, RequestTemplate template, String name) {
        Collection<String> values = headers.get(name);
        if (values != null && !values.isEmpty()) {
            template.header(name, values.iterator().next());
        }
    }
}
