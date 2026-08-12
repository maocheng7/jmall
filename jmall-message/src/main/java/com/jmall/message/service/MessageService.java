package com.jmall.message.service;
import com.jmall.api.message.dto.MessageDTO;
import com.jmall.common.core.result.PageResult;
import com.jmall.message.entity.SiteMessage;
public interface MessageService {
 boolean send(MessageDTO dto); boolean sendSms(String phone,String code);
 PageResult<SiteMessage> list(Long userId,int page,int size); long unread(Long userId); boolean read(Long userId,Long id);
}
