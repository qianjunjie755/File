package com.biz.warning.config;

import com.biz.config.redis.Jackson2JsonRedisSerializer;
import com.biz.warning.redis.RedisReceiver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisListenerConfiguration {

    private static final String RECEIVE_METHOD = "receive";

    public static final String WARN_CHANNEL = "{biz:warning}:warnChannel";

    @Bean(name = "listenerAdapter")
    public MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
        MessageListenerAdapter listener = new MessageListenerAdapter(receiver, RECEIVE_METHOD);
        listener.setSerializer(new Jackson2JsonRedisSerializer());
        return listener;
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(LettuceConnectionFactory factory,
                                                           @Qualifier("listenerAdapter") MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(listenerAdapter, new PatternTopic(WARN_CHANNEL));
        return container;
    }
}
