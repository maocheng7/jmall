package com.jmall.message.controller;
import com.jmall.api.message.dto.MessageDTO;
import com.jmall.api.message.feign.RemoteMessageService.SmsCodeRequest;
import com.jmall.common.core.result.*;
import com.jmall.common.security.context.UserContextHolder;
import com.jmall.message.entity.SiteMessage;
import com.jmall.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController @RequiredArgsConstructor @RequestMapping("/api/message")
public class MessageController { private final MessageService service;
 @PostMapping("/inner/send") public Result<Boolean> send(@RequestBody MessageDTO d){return Result.success(service.send(d));}
 @PostMapping("/inner/sms/code") public Result<Boolean> sms(@RequestBody SmsCodeRequest r){return Result.success(service.sendSms(r.getPhone(),r.getCode()));}
 @GetMapping("/list") public Result<PageResult<SiteMessage>> list(@RequestParam(defaultValue="1") int page,@RequestParam(defaultValue="10") int size){return Result.success(service.list(UserContextHolder.getUserId(),page,size));}
 @GetMapping("/unread/count") public Result<Long> unread(){return Result.success(service.unread(UserContextHolder.getUserId()));}
 @PostMapping("/{id}/read") public Result<Boolean> read(@PathVariable Long id){return Result.success(service.read(UserContextHolder.getUserId(),id));}
}
