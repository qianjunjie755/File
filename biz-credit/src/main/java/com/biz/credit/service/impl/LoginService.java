package com.biz.credit.service.impl;

import com.biz.credit.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginService implements ILoginService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void delToken() {
        redisTemplate.delete("biz_credit:tokenid");
    }
}
