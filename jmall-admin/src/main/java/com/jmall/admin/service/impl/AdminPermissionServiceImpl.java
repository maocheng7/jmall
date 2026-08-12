package com.jmall.admin.service.impl;

import com.jmall.admin.entity.AdminPermission;
import com.jmall.admin.mapper.AdminPermissionMapper;
import com.jmall.admin.service.AdminPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminPermissionServiceImpl implements AdminPermissionService {
    private final AdminPermissionMapper mapper;

    public List<AdminPermission> list() { return mapper.selectList(null); }

    public AdminPermission save(AdminPermission x) {
        if (x.getId() == null) { mapper.insert(x); } else { mapper.updateById(x); }
        return x;
    }
}
