package com.biz.search.schedule;

import com.biz.search.service.IBasicInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class LoadBasicInfoTask {

    @Autowired
    private IBasicInfoService service;

    @Autowired
    private RedisLockRegistry lockRegistry;

    @Scheduled(cron = "${biz.task-crontab.load-basic-info}")
    public void load() {
        Lock lock = lockRegistry.obtain("load-basic-info");
        if (lock.tryLock()) {
            return;
        }
        try {
            int size = 0;
            int limit = 100;
            do {
                try {
                    size = service.loadBasicInfo(limit);
                } catch (Exception e) {
                    log.error("加载企业工商基本信息数据失败: " + e.getMessage(), e);
                    size = 0;
                }
            } while (size >= limit);
            log.info("企业工商基本信息数据已全部加载完成!");
        } finally {
            lock.unlock();
        }
    }
}
