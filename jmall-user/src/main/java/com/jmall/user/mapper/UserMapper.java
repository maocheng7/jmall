package com.jmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jmall.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author jmall
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}