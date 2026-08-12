package com.jmall.logistics.service;
import com.jmall.logistics.entity.Delivery;
import com.jmall.logistics.entity.LogisticsTrack;
import java.util.List;
public interface LogisticsService { Delivery create(Delivery delivery); Delivery getByOrderNo(String orderNo); List<LogisticsTrack> tracks(Long deliveryId); }
