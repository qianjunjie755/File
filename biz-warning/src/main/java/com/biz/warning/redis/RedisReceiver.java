package com.biz.warning.redis;

public interface RedisReceiver {

    /**
     * 回调方法
     *
     * @param message 接收到的消息
     */
    void receive(Message message);
}
