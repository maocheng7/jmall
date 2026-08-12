package com.jmall.api.user.feign;

import com.jmall.api.user.dto.UserAddrDTO;
import com.jmall.api.user.dto.UserDTO;
import com.jmall.api.user.dto.UserRegisterDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户服务远程调用接口
 * <p>
 * 供 order、cart、auth 等服务调用，查询用户信息和收货地址。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-user", contextId = "remoteUserService")
public interface RemoteUserService {

    /**
     * 注册用户（auth 服务调用）
     *
     * @param dto 注册信息
     * @return 用户信息
     */
    @PostMapping("/api/user/inner/register")
    Result<UserDTO> registerUser(@RequestBody UserRegisterDTO dto);

    /**
     * 根据用户ID查询用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/api/user/inner/{userId}")
    Result<UserDTO> getUserById(@PathVariable("userId") Long userId);

    /**
     * 根据手机号查询用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @GetMapping("/api/user/inner/phone/{phone}")
    Result<UserDTO> getUserByPhone(@PathVariable("phone") String phone);

    /**
     * 查询用户的收货地址列表
     *
     * @param userId 用户ID
     * @return 地址列表
     */
    @GetMapping("/api/user/inner/{userId}/address")
    Result<List<UserAddrDTO>> listUserAddress(@PathVariable("userId") Long userId);

    /**
     * 查询用户默认收货地址
     *
     * @param userId 用户ID
     * @return 默认地址
     */
    @GetMapping("/api/user/inner/{userId}/address/default")
    Result<UserAddrDTO> getDefaultAddress(@PathVariable("userId") Long userId);
}
