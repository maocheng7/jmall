package com.jmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jmall.api.user.dto.UserDTO;
import com.jmall.api.user.dto.UserRegisterDTO;
import com.jmall.common.core.constant.CommonConstants;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.PageResult;
import com.jmall.common.core.result.ResultCode;
import com.jmall.user.entity.User;
import com.jmall.user.entity.UserAddress;
import com.jmall.user.entity.UserFavorite;
import com.jmall.user.entity.UserFootprint;
import com.jmall.user.mapper.UserAddressMapper;
import com.jmall.user.mapper.UserFavoriteMapper;
import com.jmall.user.mapper.UserFootprintMapper;
import com.jmall.user.mapper.UserMapper;
import com.jmall.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户服务实现
 *
 * @author jmall
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserAddressMapper addressMapper;
    private final UserFavoriteMapper favoriteMapper;
    private final UserFootprintMapper footprintMapper;

    // ==================== 用户基础 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO registerUser(UserRegisterDTO dto) {
        long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));
        if (count > 0) {
            throw new BusinessException(ResultCode.AUTH_PHONE_EXISTS);
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getPhone());
        user.setAvatar(dto.getAvatar());
        user.setGender(0);
        user.setLevel(1);
        user.setPoints(0);
        user.setBalance(BigDecimal.ZERO);
        user.setStatus(CommonConstants.ENABLED);
        userMapper.insert(user);
        log.info("注册新用户: userId={}, phone={}", user.getId(), dto.getPhone());
        return toDTO(user);
    }

    @Override
    public User getByPhone(String phone) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone).last("limit 1"));
    }

    @Override
    public User getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public void updateProfile(Long userId, String nickname, String avatar, Integer gender) {
        User user = getById(userId);
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId);
        if (nickname != null) wrapper.set(User::getNickname, nickname);
        if (avatar  != null) wrapper.set(User::getAvatar, avatar);
        if (gender  != null) wrapper.set(User::getGender, gender);
        userMapper.update(null, wrapper);
        log.info("更新用户资料: userId={}", userId);
    }

    @Override
    public UserDTO toDTO(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        return dto;
    }

    // ==================== 收货地址 ====================

    @Override
    public List<UserAddress> listAddress(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .orderByDesc(UserAddress::getIsDefault)
                        .orderByDesc(UserAddress::getCreateTime));
    }

    @Override
    public UserAddress getDefaultAddress(Long userId) {
        return addressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1)
                        .last("limit 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddress addAddress(Long userId, UserAddress address) {
        // 地址数量上限（20个）
        long count = addressMapper.selectCount(
                new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId, userId));
        if (count >= 20) {
            throw new BusinessException(ResultCode.USER_ADDRESS_LIMIT);
        }
        address.setUserId(userId);
        // 设为默认时先清除旧默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }
        addressMapper.insert(address);
        log.info("新增收货地址: userId={}, addressId={}", userId, address.getId());
        return address;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userId, Long addressId, UserAddress address) {
        UserAddress existing = addressMapper.selectById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "地址不存在或无权操作");
        }
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefaultAddress(userId);
        }
        address.setId(addressId);
        address.setUserId(userId);
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress existing = addressMapper.selectById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "地址不存在或无权操作");
        }
        addressMapper.deleteById(addressId);
        log.info("删除收货地址: userId={}, addressId={}", userId, addressId);
    }

    /** 清除用户所有地址的默认状态 */
    private void clearDefaultAddress(Long userId) {
        addressMapper.update(null,
                new LambdaUpdateWrapper<UserAddress>()
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDefault, 1)
                        .set(UserAddress::getIsDefault, 0));
    }

    // ==================== 收藏 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFavorite(Long userId, Long spuId) {
        if (isFavorite(userId, spuId)) {
            throw new BusinessException(ResultCode.USER_FAVORITE_EXISTS);
        }
        UserFavorite fav = new UserFavorite();
        fav.setUserId(userId);
        fav.setSpuId(spuId);
        favoriteMapper.insert(fav);
        log.info("添加收藏: userId={}, spuId={}", userId, spuId);
    }

    @Override
    public void removeFavorite(Long userId, Long spuId) {
        int deleted = favoriteMapper.delete(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getSpuId, spuId));
        if (deleted > 0) {
            log.info("取消收藏: userId={}, spuId={}", userId, spuId);
        }
    }

    @Override
    public PageResult<UserFavorite> listFavorite(Long userId, int page, int size) {
        Page<UserFavorite> p = new Page<>(page, size);
        Page<UserFavorite> result = favoriteMapper.selectPage(p,
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .orderByDesc(UserFavorite::getCreateTime));
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public boolean isFavorite(Long userId, Long spuId) {
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getUserId, userId)
                        .eq(UserFavorite::getSpuId, spuId)) > 0;
    }

    // ==================== 足迹 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFootprint(Long userId, Long spuId, Long skuId) {
        // 同一天同一商品只记录一次
        long count = footprintMapper.selectCount(
                new LambdaQueryWrapper<UserFootprint>()
                        .eq(UserFootprint::getUserId, userId)
                        .eq(UserFootprint::getSpuId, spuId)
                        .ge(UserFootprint::getCreateTime, java.time.LocalDate.now().atStartOfDay()));
        if (count > 0) return; // 今日已记录，跳过

        UserFootprint fp = new UserFootprint();
        fp.setUserId(userId);
        fp.setSpuId(spuId);
        fp.setSkuId(skuId);
        footprintMapper.insert(fp);
    }

    @Override
    public PageResult<UserFootprint> listFootprint(Long userId, int page, int size) {
        Page<UserFootprint> p = new Page<>(page, size);
        Page<UserFootprint> result = footprintMapper.selectPage(p,
                new LambdaQueryWrapper<UserFootprint>()
                        .eq(UserFootprint::getUserId, userId)
                        .orderByDesc(UserFootprint::getCreateTime));
        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords());
    }
}
