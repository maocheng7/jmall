package com.jmall.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 认证账号实体（对应表 auth_account）
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth_account")
public class AuthAccount extends BaseEntity {

    /**
     * 用户ID（关联 jmall_user.user.id）
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（BCrypt 加密）
     */
    private String password;

    /**
     * 微信小程序 openid
     */
    private String wxOpenid;

    /**
     * 微信开放平台 unionid
     */
    private String wxUnionid;

    /**
     * 账号类型：1=密码，2=短信，3=微信
     */
    private Integer loginType;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}