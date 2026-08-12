package com.jmall.merchant.entity;
import com.baomidou.mybatisplus.annotation.TableName; import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data; import lombok.EqualsAndHashCode; import java.math.BigDecimal;
@Data @EqualsAndHashCode(callSuper=true) @TableName("shop")
public class Shop extends BaseEntity { private Long merchantId; private String shopName; private String logo; private String banner; private String description; private String notice; private String phone; private String province; private String city; private String address; private BigDecimal score; private Integer status; }
