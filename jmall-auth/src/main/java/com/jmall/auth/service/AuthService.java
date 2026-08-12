package com.jmall.auth.service;

import com.jmall.auth.dto.LoginDTO;
import com.jmall.auth.dto.RegisterDTO;
import com.jmall.auth.dto.SmsSendDTO;
import com.jmall.auth.vo.AuthLoginVO;

/**
 * 认证服务接口
 *
 * @author jmall
 */
public interface AuthService {

    /**
     * 手机号注册
     */
    AuthLoginVO register(RegisterDTO dto);

    /**
     * 登录（密码/短信/微信三种方式）
     */
    AuthLoginVO login(LoginDTO dto);

    /**
     * 发送短信验证码
     */
    void sendSmsCode(SmsSendDTO dto);

    /**
     * 登出（清除 Token）
     */
    void logout();
}
