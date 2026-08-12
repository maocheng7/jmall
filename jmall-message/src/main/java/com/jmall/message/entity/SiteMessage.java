package com.jmall.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jmall.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("site_message")
public class SiteMessage extends BaseEntity {
    private Long userId;
    private String title;
    private String content;
    private Integer msgType;
    private String linkUrl;
    private Integer isRead;
    private LocalDateTime readTime;
}
