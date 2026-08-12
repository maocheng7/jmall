package com.jmall.merchant.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data; import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
@Data @EqualsAndHashCode(callSuper=true) @TableName("merchant_apply")
public class MerchantApply extends BaseEntity {
 private Long userId; private Long merchantId; private String companyName; private String companyCode;
 private String contactName; private String contactPhone; private String address; private String licenseImage;
 private Integer status; private String auditRemark; private LocalDateTime auditTime; private LocalDateTime applyTime;
}
