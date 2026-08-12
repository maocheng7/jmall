package com.jmall.api.stock.feign;

import com.jmall.api.stock.dto.StockDeductDTO;
import com.jmall.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 库存服务远程调用接口
 * <p>
 * 供 order 服务调用，执行库存扣减和回滚。
 * </p>
 *
 * @author jmall
 */
@FeignClient(name = "jmall-stock", contextId = "remoteStockService")
public interface RemoteStockService {

    /**
     * 扣减库存
     *
     * @param dto 库存扣减请求
     * @return 结果
     */
    @PostMapping("/api/stock/inner/deduct")
    Result<Boolean> deductStock(@RequestBody StockDeductDTO dto);

    /**
     * 回滚库存（订单取消时调用）
     *
     * @param orderNo 订单号
     * @return 结果
     */
    @PostMapping("/api/stock/inner/rollback/{orderNo}")
    Result<Boolean> rollbackStock(@PathVariable("orderNo") String orderNo);

    /**
     * 查询 SKU 库存
     *
     * @param skuId SKU ID
     * @return 库存数量
     */
    @GetMapping("/api/stock/inner/{skuId}")
    Result<Integer> getStock(@PathVariable("skuId") Long skuId);
}
