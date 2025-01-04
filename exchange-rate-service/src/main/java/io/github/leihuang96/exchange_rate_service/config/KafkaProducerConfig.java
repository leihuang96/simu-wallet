package io.github.leihuang96.exchange_rate_service.config;

import io.github.leihuang96.common_module.ConversionResponseEvent;
import io.github.leihuang96.common_module.TransactionEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * 通用生产者配置
     */
    private Map<String, Object> commonProducerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return config;
    }

    /**
     * 创建 Kafka 生产者工厂，用于 TransactionEvent
     */
    @Bean
    public ProducerFactory<String, TransactionEvent> transactionEventProducerFactory() {
        Map<String, Object> config = new HashMap<>(commonProducerConfigs());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * 创建 KafkaTemplate，用于 TransactionEvent
     */
    @Bean(name = "transactionEventKafkaTemplate")
    public KafkaTemplate<String, TransactionEvent> transactionEventKafkaTemplate() {
        return new KafkaTemplate<>(transactionEventProducerFactory());
    }

    /**
     * 创建 Kafka 生产者工厂，用于 ConversionResponseEvent
     */
    @Bean
    public ProducerFactory<String, ConversionResponseEvent> conversionResponseEventProducerFactory() {
        Map<String, Object> config = new HashMap<>(commonProducerConfigs());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * 创建 KafkaTemplate，用于 ConversionResponseEvent
     */
    @Bean(name = "conversionResponseEventKafkaTemplate")
    public KafkaTemplate<String, ConversionResponseEvent> conversionResponseEventKafkaTemplate() {
        return new KafkaTemplate<>(conversionResponseEventProducerFactory());
    }

}
