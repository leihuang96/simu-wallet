package io.github.leihuang96.exchange_rate_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 配置 RedisTemplate 用于操作 Redis 数据
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        System.out.println("Connecting to Redis with factory: " + connectionFactory);

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用 String 序列化器，确保键和值以字符串形式存储
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

    /**
     * 自定义 RedisConnectionFactory，支持动态主机和端口配置
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(Environment environment) {
        String host = environment.getProperty("spring.redis.host", "redis");
        int port = Integer.parseInt(environment.getProperty("spring.redis.port", "6379"));
        String password = environment.getProperty("spring.redis.password", "");

        LettuceConnectionFactory factory = new LettuceConnectionFactory(host, port);
        if (!password.isEmpty()) {
            factory.setPassword(password);
        }

        // 确保 Factory 完全初始化
        factory.afterPropertiesSet();
        return factory;
    }
}
