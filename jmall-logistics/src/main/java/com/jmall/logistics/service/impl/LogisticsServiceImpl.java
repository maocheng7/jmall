package com.jmall.logistics.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jmall.logistics.entity.*; import com.jmall.logistics.mapper.*; import com.jmall.logistics.service.LogisticsService;
import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import java.util.*;
@Service @RequiredArgsConstructor public class LogisticsServiceImpl implements LogisticsService { private final DeliveryMapper deliveryMapper; private final LogisticsTrackMapper trackMapper;
 public Delivery create(Delivery d){if(d.getDeliveryNo()==null)d.setDeliveryNo("D"+System.currentTimeMillis());if(d.getLogisticsNo()==null)d.setLogisticsNo(d.getDeliveryNo());if(d.getLogisticsCom()==null)d.setLogisticsCom("SF");if(d.getStatus()==null)d.setStatus(0);deliveryMapper.insert(d); LogisticsTrack t=new LogisticsTrack();t.setDeliveryId(d.getId());t.setLogisticsNo(d.getLogisticsNo());t.setTrackInfo("物流单已创建，等待揽收（mock）");t.setTrackStatus(0);trackMapper.insert(t);return d;}
 public Delivery getByOrderNo(String no){return deliveryMapper.selectOne(new LambdaQueryWrapper<Delivery>().eq(Delivery::getOrderNo,no));}
 public List<LogisticsTrack> tracks(Long id){return trackMapper.selectList(new LambdaQueryWrapper<LogisticsTrack>().eq(LogisticsTrack::getDeliveryId,id).orderByDesc(LogisticsTrack::getTrackTime));}
}
