package com.jmall.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jmall.auth.entity.AuthAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 认证账号 Mapper
 *
 * @author jmall
 */
@Mapper
public interface AuthAccountMapper extends BaseMapper<AuthAccount> {
}