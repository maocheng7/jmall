package com.jmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jmall.user.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏 Mapper
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
}
