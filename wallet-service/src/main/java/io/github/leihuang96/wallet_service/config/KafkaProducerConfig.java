package io.github.leihuang96.wallet_service.config;

import io.github.leihuang96.common_module.TransactionEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Kafka 生产者配置
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 添加追踪拦截器
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                TracingProducerInterceptor.class.getName());

        return props;}

    // Kafka 模板
    @Bean
    public KafkaTemplate<String, TransactionEvent> kafkaTemplate() {
        //DefaultKafkaProducerFactory 使用提供的producerConfigs配置来初始化生产者实例。
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerConfigs()));
    }
}