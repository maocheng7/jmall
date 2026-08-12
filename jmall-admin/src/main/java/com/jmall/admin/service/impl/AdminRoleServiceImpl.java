package com.jmall.admin.service.impl;

import com.jmall.admin.entity.AdminRole;
import com.jmall.admin.mapper.AdminRoleMapper;
import com.jmall.admin.service.AdminRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoleServiceImpl implements AdminRoleService {
    private final AdminRoleMapper mapper;

    public List<AdminRole> list() { return mapper.selectList(null); }

    public AdminRole save(AdminRole x) {
        if (x.getId() == null) { mapper.insert(x); } else { mapper.updateById(x); }
        return x;
    }
}
