package com.jmall.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jmall.stock.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {
}
