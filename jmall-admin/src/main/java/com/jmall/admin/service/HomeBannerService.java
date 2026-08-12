package com.jmall.admin.service;
import com.jmall.admin.entity.*; import java.util.List;
public interface HomeBannerService { List<HomeBanner> list(); HomeBanner save(HomeBanner x); void delete(Long id); }
