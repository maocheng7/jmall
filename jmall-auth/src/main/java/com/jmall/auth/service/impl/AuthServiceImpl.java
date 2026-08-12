package com.jmall.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jmall.api.user.dto.UserDTO;
import com.jmall.api.user.dto.UserRegisterDTO;
import com.jmall.api.user.feign.RemoteUserService;
import com.jmall.auth.dto.LoginDTO;
import com.jmall.auth.dto.RegisterDTO;
import com.jmall.auth.dto.SmsSendDTO;
import com.jmall.auth.entity.AuthAccount;
import com.jmall.auth.mapper.AuthAccountMapper;
import com.jmall.auth.service.AuthService;
import com.jmall.auth.vo.AuthLoginVO;
import com.jmall.common.core.constant.RedisConstants;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.ResultCode;
import com.jmall.common.core.result.Result;
import com.jmall.common.redis.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 认证服务实现
 * <p>
 * 核心流程：
 * 1. 注册：校验验证码 → 调 user 服务创建用户 → 创建 auth_account → Sa-Token 登录
 * 2. 密码登录：查 account → BCrypt 校验 → Sa-Token 登录
 * 3. 短信登录：校验验证码 → 查/创 account → Sa-Token 登录
 * 4. 微信登录：查 account by openid → 不存在则创建 → Sa-Token 登录
 * </p>
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthAccountMapper authAccountMapper;
    private final RemoteUserService remoteUserService;
    private final CacheUtils cacheUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==================== 注册 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthLoginVO register(RegisterDTO dto) {
        // 1. 校验短信验证码
        verifySmsCode(dto.getPhone(), dto.getCode(), "register");

        // 2. 手机号是否已注册
        AuthAccount existAccount = getAccountByPhone(dto.getPhone());
        if (existAccount != null) {
            throw new BusinessException(ResultCode.AUTH_PHONE_EXISTS);
        }

        // 3. 调用 user 服务创建用户
        UserRegisterDTO userReg = new UserRegisterDTO();
        userReg.setPhone(dto.getPhone());
        userReg.setUsername(dto.getUsername());
        userReg.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getPhone());
        Result<UserDTO> userResult = remoteUserService.registerUser(userReg);
        if (!userResult.isSuccess() || userResult.getData() == null) {
            throw new BusinessException(ResultCode.AUTH_PHONE_EXISTS.getCode(), userResult.getMessage());
        }
        UserDTO userDTO = userResult.getData();

        // 4. 创建本地认证账号
        AuthAccount account = new AuthAccount();
        account.setUserId(userDTO.getUserId());
        account.setPhone(dto.getPhone());
        account.setUsername(dto.getUsername());
        account.setPassword(passwordEncoder.encode(dto.getPassword()));
        account.setLoginType(1);
        account.setStatus(1);
        authAccountMapper.insert(account);
        log.info("用户注册成功: userId={}, phone={}", userDTO.getUserId(), dto.getPhone());

        // 5. Sa-Token 登录
        return doLogin(userDTO, "user");
    }

    // ==================== 登录 ====================

    @Override
    public AuthLoginVO login(LoginDTO dto) {
        return switch (dto.getLoginType()) {
            case 1 -> loginByPassword(dto);
            case 2 -> loginBySms(dto);
            case 3 -> loginByWechat(dto);
            default -> throw new BusinessException(ResultCode.BAD_REQUEST, "不支持的登录方式");
        };
    }

    /**
     * 密码登录
     */
    private AuthLoginVO loginByPassword(LoginDTO dto) {
        if (dto.getPhone() == null || dto.getPassword() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "手机号和密码不能为空");
        }

        AuthAccount account = getAccountByPhone(dto.getPhone());
        if (account == null) {
            throw new BusinessException(ResultCode.AUTH_ACCOUNT_NOT_FOUND);
        }
        if (account.getStatus() == 0) {
            throw new BusinessException(ResultCode.AUTH_ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword())) {
            throw new BusinessException(ResultCode.AUTH_PASSWORD_ERROR);
        }

        // 查询用户信息
        UserDTO userDTO = getUserInfo(account.getUserId());
        updateLoginTime(account, null);
        return doLogin(userDTO, "user");
    }

    /**
     * 短信验证码登录
     */
    private AuthLoginVO loginBySms(LoginDTO dto) {
        if (dto.getPhone() == null || dto.getCode() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "手机号和验证码不能为空");
        }

        // 校验验证码
        verifySmsCode(dto.getPhone(), dto.getCode(), "login");

        AuthAccount account = getAccountByPhone(dto.getPhone());
        if (account == null) {
            // 自动注册（短信登录首次即注册）
            UserRegisterDTO userReg = new UserRegisterDTO();
            userReg.setPhone(dto.getPhone());
            userReg.setNickname(dto.getPhone());
            Result<UserDTO> userResult = remoteUserService.registerUser(userReg);
            if (!userResult.isSuccess() || userResult.getData() == null) {
                throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "自动注册失败");
            }
            UserDTO userDTO = userResult.getData();

            account = new AuthAccount();
            account.setUserId(userDTO.getUserId());
            account.setPhone(dto.getPhone());
            account.setLoginType(2);
            account.setStatus(1);
            authAccountMapper.insert(account);
            log.info("短信登录自动注册: userId={}", userDTO.getUserId());
            return doLogin(userDTO, "user");
        }

        UserDTO userDTO = getUserInfo(account.getUserId());
        updateLoginTime(account, null);
        return doLogin(userDTO, "user");
    }

    /**
     * 微信登录
     */
    private AuthLoginVO loginByWechat(LoginDTO dto) {
        if (dto.getWxOpenid() == null || dto.getWxOpenid().isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "微信 openid 不能为空");
        }

        // 根据 openid 查找已有账号
        AuthAccount account = authAccountMapper.selectOne(
                new LambdaQueryWrapper<AuthAccount>()
                        .eq(AuthAccount::getWxOpenid, dto.getWxOpenid())
                        .last("limit 1"));

        if (account == null) {
            // 未绑定过，自动创建用户
            UserRegisterDTO userReg = new UserRegisterDTO();
            userReg.setPhone("wx_" + System.currentTimeMillis()); // 临时手机号占位
            userReg.setNickname("微信用户");
            Result<UserDTO> userResult = remoteUserService.registerUser(userReg);
            if (!userResult.isSuccess() || userResult.getData() == null) {
                throw new BusinessException(ResultCode.AUTH_WX_LOGIN_ERROR);
            }
            UserDTO userDTO = userResult.getData();

            account = new AuthAccount();
            account.setUserId(userDTO.getUserId());
            account.setWxOpenid(dto.getWxOpenid());
            account.setLoginType(3);
            account.setStatus(1);
            authAccountMapper.insert(account);
            log.info("微信登录自动创建用户: userId={}, openid={}", userDTO.getUserId(), dto.getWxOpenid());
            return doLogin(userDTO, "user");
        }

        if (account.getStatus() == 0) {
            throw new BusinessException(ResultCode.AUTH_ACCOUNT_DISABLED);
        }

        UserDTO userDTO = getUserInfo(account.getUserId());
        updateLoginTime(account, null);
        return doLogin(userDTO, "user");
    }

    // ==================== 验证码 ====================

    @Override
    public void sendSmsCode(SmsSendDTO dto) {
        String phone = dto.getPhone();
        String scene = dto.getScene();

        // 60 秒防重复
        String countKey = RedisConstants.AUTH_CODE_KEY + "cnt:" + phone + ":" + scene;
        if (Boolean.TRUE.equals(cacheUtils.hasKey(countKey))) {
            throw new BusinessException(ResultCode.AUTH_CODE_SEND_TOO_FREQUENT);
        }

        // 生成 6 位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 存入 Redis，5 分钟有效
        String codeKey = RedisConstants.AUTH_CODE_KEY + phone + ":" + scene;
        cacheUtils.set(codeKey, code, RedisConstants.AUTH_CODE_TTL);
        // 发送间隔限制
        cacheUtils.set(countKey, "1", 60);

        // TODO: 接入真实短信服务（第7步 message 服务对接）
        // 当前开发阶段直接打印日志
        log.info("【短信验证码】phone={}, scene={}, code={}", phone, scene, code);
    }

    // ==================== 登出 ====================

    @Override
    public void logout() {
        StpUtil.logout();
        log.info("用户登出: userId={}", StpUtil.getLoginIdAsLong());
    }

    // ==================== 私有方法 ====================

    /**
     * Sa-Token 登录核心逻辑
     */
    private AuthLoginVO doLogin(UserDTO userDTO, String role) {
        // Sa-Token 登录，userId 为 loginId
        StpUtil.login(userDTO.getUserId());

        // 将角色信息存入 Token Session
        StpUtil.getTokenSession().set("role", role);
        StpUtil.getTokenSession().set("username", userDTO.getUsername());

        String tokenValue = StpUtil.getTokenValue();
        log.info("登录成功: userId={}, token={}", userDTO.getUserId(), tokenValue);

        return new AuthLoginVO(
                tokenValue,
                "Bearer",
                userDTO.getUserId(),
                userDTO.getUsername(),
                userDTO.getNickname(),
                userDTO.getAvatar(),
                role
        );
    }

    /**
     * 查询用户信息（跨服务调用）
     */
    private UserDTO getUserInfo(Long userId) {
        Result<UserDTO> result = remoteUserService.getUserById(userId);
        if (!result.isSuccess() || result.getData() == null) {
            throw new BusinessException(ResultCode.AUTH_ACCOUNT_NOT_FOUND);
        }
        return result.getData();
    }

    /**
     * 根据手机号查询本地认证账号
     */
    private AuthAccount getAccountByPhone(String phone) {
        return authAccountMapper.selectOne(
                new LambdaQueryWrapper<AuthAccount>()
                        .eq(AuthAccount::getPhone, phone)
                        .last("limit 1"));
    }

    /**
     * 校验短信验证码
     */
    private void verifySmsCode(String phone, String code, String scene) {
        String codeKey = RedisConstants.AUTH_CODE_KEY + phone + ":" + scene;
        Object cached = cacheUtils.get(codeKey);
        if (cached == null) {
            throw new BusinessException(ResultCode.AUTH_CODE_ERROR);
        }
        if (!code.equals(cached.toString())) {
            throw new BusinessException(ResultCode.AUTH_CODE_ERROR);
        }
        // 验证通过，删除验证码
        cacheUtils.delete(codeKey);
    }

    /**
     * 更新最后登录信息
     */
    private void updateLoginTime(AuthAccount account, String ip) {
        account.setLastLoginTime(LocalDateTime.now());
        if (ip != null) {
            account.setLastLoginIp(ip);
        }
        authAccountMapper.updateById(account);
    }
}
