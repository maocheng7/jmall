package com.jmall.merchant.entity;
import com.baomidou.mybatisplus.annotation.TableName; import com.jmall.common.mybatis.base.BaseEntity; import lombok.Data; import lombok.EqualsAndHashCode; import java.time.LocalDateTime;
@Data @EqualsAndHashCode(callSuper=true) @TableName("chat_session")
public class ChatSession extends BaseEntity { private String sessionNo; private Long userId; private Long merchantId; private Long shopId; private Integer status; private String lastMessage; private LocalDateTime lastTime; }
