package com.biz.warning.service.impl;

import com.biz.warning.redis.Message;
import com.biz.warning.redis.RedisReceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisReceiverImpl implements RedisReceiver {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void receive(Message message) {
        log.info("接收到预警通知消息: {}", message);
        if (message == null) {
            return;
        }
        simpMessagingTemplate.convertAndSendToUser(message.getUserId(), "/notice", message.getContent());
    }
}
