package com.jmall.logistics.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
@Data @EqualsAndHashCode(callSuper=true) @TableName("logistics_track")
public class LogisticsTrack extends BaseEntity {
 private Long deliveryId; private String logisticsNo; private String trackInfo; private Integer trackStatus; private String operator; private LocalDateTime trackTime;
}
