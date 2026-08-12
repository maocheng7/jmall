package com.jmall.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户实体（对应表 user）
 * <p>
 * 第3步先提供认证闭环所需字段，其余字段随第4步用户服务完善。
 * </p>
 *
 * @author jmall
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0=未知，1=男，2=女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 会员等级：1=普通，2=银卡，3=金卡，4=钻石
     */
    private Integer level;

    /**
     * 积分余额
     */
    private Integer points;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 状态：0=禁用，1=启用
     */
    private Integer status;
}