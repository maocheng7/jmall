package com.jmall.logistics.controller;
import com.jmall.common.core.result.Result; import com.jmall.logistics.entity.*; import com.jmall.logistics.service.LogisticsService; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/logistics") public class LogisticsController { private final LogisticsService service;
 @PostMapping("/delivery") public Result<Delivery> create(@RequestBody Delivery d){return Result.success(service.create(d));}
 @GetMapping("/{orderNo}") public Result<Delivery> byOrder(@PathVariable String orderNo){return Result.success(service.getByOrderNo(orderNo));}
 @GetMapping("/{deliveryId}/tracks") public Result<List<LogisticsTrack>> tracks(@PathVariable Long deliveryId){return Result.success(service.tracks(deliveryId));}
}
