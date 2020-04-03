package com.biz.warning.redis;

import com.biz.warning.config.RedisListenerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisNotice {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发布消息
     *
     * @param userId
     * @param message
     */
    public void push(Integer userId, Object message) {
        Message _message = new Message(String.valueOf(userId), message);
        log.info("发送预警通知: {}", _message);
        redisTemplate.convertAndSend(RedisListenerConfiguration.WARN_CHANNEL, _message);
    }
}
