package com.jmall.admin.service.impl;

import com.jmall.admin.entity.SysConfig;
import com.jmall.admin.mapper.SysConfigMapper;
import com.jmall.admin.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {
    private final SysConfigMapper mapper;

    public List<SysConfig> list() { return mapper.selectList(null); }

    public SysConfig save(SysConfig x) {
        if (x.getId() == null) { mapper.insert(x); } else { mapper.updateById(x); }
        return x;
    }
}
