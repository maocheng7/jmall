package com.jmall.logistics.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
@Data @EqualsAndHashCode(callSuper=true) @TableName("delivery")
public class Delivery extends BaseEntity {
 private String deliveryNo; private String orderNo; private String logisticsNo; private String logisticsCom;
 private String receiverName; private String receiverPhone; private String receiverAddress; private String senderName;
 private Integer status; private LocalDateTime expectArriveTime; private LocalDateTime signTime; private String remark;
}
