package com.jmall.admin.service.impl;

import com.jmall.admin.entity.HomeBanner;
import com.jmall.admin.mapper.HomeBannerMapper;
import com.jmall.admin.service.HomeBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeBannerServiceImpl implements HomeBannerService {
    private final HomeBannerMapper mapper;

    public List<HomeBanner> list() { return mapper.selectList(null); }

    public HomeBanner save(HomeBanner x) {
        if (x.getId() == null) { mapper.insert(x); } else { mapper.updateById(x); }
        return x;
    }

    public void delete(Long id) { mapper.deleteById(id); }
}
