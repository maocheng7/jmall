package com.jmall.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存流水
 *
 * @author jmall
 */
@Data
@TableName("stock_record")
public class StockRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderNo;
    private Long skuId;
    private Integer quantity;
    /** 1=预占锁定，2=确认扣减，3=回滚释放 */
    private Integer type;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String bizId;
    private LocalDateTime createTime;
}
