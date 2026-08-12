package com.jmall.api.message.feign;

import com.jmall.api.message.dto.MessageDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 消息服务远程调用接口
 * <p>
 * 供 auth、order 等服务调用，发送短信、站内信和推送通知。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-message", contextId = "remoteMessageService")
public interface RemoteMessageService {

    /**
     * 发送消息（统一入口，根据 MessageDTO.type 分发）
     *
     * @param dto 消息内容
     * @return 结果
     */
    @PostMapping("/api/message/inner/send")
    Result<Boolean> sendMessage(@RequestBody MessageDTO dto);

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 结果
     */
    @PostMapping("/api/message/inner/sms/code")
    Result<Boolean> sendSmsCode(@RequestBody SmsCodeRequest request);

    /**
     * 短信验证码请求参数
     */
    @lombok.Data
    class SmsCodeRequest implements java.io.Serializable {
        @java.io.Serial
        private static final long serialVersionUID = 1L;
        private String phone;
        private String code;
    }
}
