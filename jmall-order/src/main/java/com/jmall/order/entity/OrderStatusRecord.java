package com.jmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单状态流转
 *
 * @author jmall
 */
@Data
@TableName("order_status_record")
public class OrderStatusRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderNo;
    private Integer fromStatus;
    private Integer toStatus;
    private String remark;
    private String operator;
    private LocalDateTime createTime;
}
