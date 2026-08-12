package com.jmall.admin.service.impl;

import com.jmall.admin.entity.DailyReport;
import com.jmall.admin.mapper.DailyReportMapper;
import com.jmall.admin.service.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyReportServiceImpl implements DailyReportService {
    private final DailyReportMapper mapper;

    public List<DailyReport> list() { return mapper.selectList(null); }
}
