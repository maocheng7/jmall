package com.jmall.admin.service.impl;

import com.jmall.admin.entity.AdminUser;
import com.jmall.admin.mapper.AdminUserMapper;
import com.jmall.admin.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final AdminUserMapper mapper;

    public List<AdminUser> list() { return mapper.selectList(null); }

    public AdminUser save(AdminUser x) {
        if (x.getId() == null) { mapper.insert(x); } else { mapper.updateById(x); }
        return x;
    }
}
