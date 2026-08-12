package com.jmall.user.service;

import com.jmall.api.user.dto.UserDTO;
import com.jmall.api.user.dto.UserRegisterDTO;
import com.jmall.common.core.result.PageResult;
import com.jmall.user.entity.User;
import com.jmall.user.entity.UserAddress;
import com.jmall.user.entity.UserFavorite;
import com.jmall.user.entity.UserFootprint;

import java.util.List;

/**
 * 用户服务接口
 *
 * @author jmall
 */
public interface UserService {

    // ==================== 用户基础 ====================

    /** 注册用户（auth服务远程调用） */
    UserDTO registerUser(UserRegisterDTO dto);

    /** 根据手机号查询 */
    User getByPhone(String phone);

    /** 根据ID查询 */
    User getById(Long userId);

    /** 更新用户资料 */
    void updateProfile(Long userId, String nickname, String avatar, Integer gender);

    /** 实体转DTO */
    UserDTO toDTO(User user);

    // ==================== 收货地址 ====================

    /** 查询用户地址列表 */
    List<UserAddress> listAddress(Long userId);

    /** 查询默认地址 */
    UserAddress getDefaultAddress(Long userId);

    /** 新增地址 */
    UserAddress addAddress(Long userId, UserAddress address);

    /** 更新地址 */
    void updateAddress(Long userId, Long addressId, UserAddress address);

    /** 删除地址 */
    void deleteAddress(Long userId, Long addressId);

    // ==================== 收藏 ====================

    /** 添加收藏 */
    void addFavorite(Long userId, Long spuId);

    /** 取消收藏 */
    void removeFavorite(Long userId, Long spuId);

    /** 收藏列表（分页） */
    PageResult<UserFavorite> listFavorite(Long userId, int page, int size);

    /** 判断是否已收藏 */
    boolean isFavorite(Long userId, Long spuId);

    // ==================== 足迹 ====================

    /** 记录浏览足迹 */
    void addFootprint(Long userId, Long spuId, Long skuId);

    /** 足迹列表（分页） */
    PageResult<UserFootprint> listFootprint(Long userId, int page, int size);
}
