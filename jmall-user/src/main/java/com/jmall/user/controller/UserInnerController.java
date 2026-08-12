package com.jmall.user.controller;

import com.jmall.api.user.dto.UserAddrDTO;
import com.jmall.api.user.dto.UserDTO;
import com.jmall.api.user.dto.UserRegisterDTO;
import com.jmall.common.core.exception.BusinessException;
import com.jmall.common.core.result.Result;
import com.jmall.common.core.result.ResultCode;
import com.jmall.user.entity.User;
import com.jmall.user.entity.UserAddress;
import com.jmall.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户内部接口（供其他服务 Feign 调用）
 *
 * @author jmall
 */
@Tag(name = "用户内部接口")
@RestController
@RequestMapping("/api/user/inner")
@RequiredArgsConstructor
public class UserInnerController {

    private final UserService userService;

    @Operation(summary = "注册用户（内部）")
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody UserRegisterDTO dto) {
        return Result.success(userService.registerUser(dto));
    }

    @Operation(summary = "根据ID查询用户（内部）")
    @GetMapping("/{userId}")
    public Result<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        UserDTO dto = userService.toDTO(userService.getById(userId));
        return Result.success(dto);
    }

    @Operation(summary = "根据手机号查询用户（内部）")
    @GetMapping("/phone/{phone}")
    public Result<UserDTO> getUserByPhone(@PathVariable("phone") String phone) {
        User user = userService.getByPhone(phone);
        return Result.success(userService.toDTO(user));
    }

    @Operation(summary = "查询用户地址列表（内部）")
    @GetMapping("/{userId}/address")
    public Result<List<UserAddrDTO>> listUserAddress(@PathVariable("userId") Long userId) {
        List<UserAddress> list = userService.listAddress(userId);
        List<UserAddrDTO> dtoList = list.stream().map(this::toAddrDTO).collect(Collectors.toList());
        return Result.success(dtoList);
    }

    @Operation(summary = "查询用户默认地址（内部）")
    @GetMapping("/{userId}/address/default")
    public Result<UserAddrDTO> getDefaultAddress(@PathVariable("userId") Long userId) {
        UserAddress addr = userService.getDefaultAddress(userId);
        return Result.success(addr != null ? toAddrDTO(addr) : null);
    }

    /** 地址实体转 DTO */
    private UserAddrDTO toAddrDTO(UserAddress addr) {
        UserAddrDTO dto = new UserAddrDTO();
        dto.setId(addr.getId());
        dto.setUserId(addr.getUserId());
        dto.setReceiverName(addr.getReceiverName());
        dto.setReceiverPhone(addr.getReceiverPhone());
        dto.setProvince(addr.getProvince());
        dto.setCity(addr.getCity());
        dto.setDistrict(addr.getDistrict());
        dto.setDetailAddress(addr.getDetailAddress());
        dto.setIsDefault(addr.getIsDefault());
        return dto;
    }
}
