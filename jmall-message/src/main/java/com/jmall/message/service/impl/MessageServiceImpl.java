package com.jmall.message.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jmall.api.message.dto.MessageDTO;
import com.jmall.common.core.result.PageResult;
import com.jmall.message.entity.MessageRecord;
import com.jmall.message.entity.SiteMessage;
import com.jmall.message.mapper.MessageRecordMapper;
import com.jmall.message.mapper.SiteMessageMapper;
import com.jmall.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service @RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
 private final MessageRecordMapper recordMapper; private final SiteMessageMapper siteMapper; private final ObjectMapper objectMapper=new ObjectMapper();
 public boolean send(MessageDTO dto){ MessageRecord r=new MessageRecord(); r.setType(dto.getType()); r.setReceiver(dto.getReceiver()); r.setTemplateCode(dto.getTemplateCode()); r.setBizId(dto.getBizId()); r.setStatus(1); r.setSendTime(LocalDateTime.now()); try{r.setParams(objectMapper.writeValueAsString(dto.getParams()));}catch(Exception ignored){} recordMapper.insert(r); if("site".equals(dto.getType())){SiteMessage s=new SiteMessage(); try{s.setUserId(Long.valueOf(dto.getReceiver()));}catch(Exception ignored){} s.setTitle(dto.getTemplateCode()==null?"系统消息":dto.getTemplateCode()); s.setContent(dto.getParams()==null?"":dto.getParams().toString()); s.setIsRead(0); siteMapper.insert(s);} return true; }
 public boolean sendSms(String phone,String code){ MessageDTO d=new MessageDTO(); d.setType("sms"); d.setReceiver(phone); return send(d); }
 public PageResult<SiteMessage> list(Long uid,int page,int size){Page<SiteMessage> p=siteMapper.selectPage(new Page<>(page,size),new LambdaQueryWrapper<SiteMessage>().eq(SiteMessage::getUserId,uid).orderByDesc(SiteMessage::getCreateTime)); return PageResult.of(p.getTotal(),p.getCurrent(),p.getSize(),p.getRecords());}
 public long unread(Long uid){return siteMapper.selectCount(new LambdaQueryWrapper<SiteMessage>().eq(SiteMessage::getUserId,uid).eq(SiteMessage::getIsRead,0));}
 public boolean read(Long uid,Long id){return siteMapper.update(null,new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<SiteMessage>().eq(SiteMessage::getId,id).eq(SiteMessage::getUserId,uid).set(SiteMessage::getIsRead,1).set(SiteMessage::getReadTime,LocalDateTime.now()))>0;}
}
