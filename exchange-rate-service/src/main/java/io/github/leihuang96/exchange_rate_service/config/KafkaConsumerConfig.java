package io.github.leihuang96.exchange_rate_service.config;

import io.github.leihuang96.common_module.ConversionResponseEvent;
import io.github.leihuang96.common_module.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaConsumerConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    /**
     * 通用消费者配置
     */
    private Map<String, Object> commonConsumerConfigs() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "exchange-rate-group");
        return config;
    }


    /**
     * 创建 Kafka 消费者工厂，用于 TransactionEvent
     */
    @Bean
    public ConsumerFactory<String, TransactionEvent> conversionRequestConsumerFactory() {
        logger.info("Creating Kafka Consumer Factory for TransactionEvent");
        return new DefaultKafkaConsumerFactory<>(
                commonConsumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(TransactionEvent.class));
    }

    /**
     * 创建 KafkaListenerContainerFactory，用于 TransactionEvent
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> conversionRequestContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(conversionRequestConsumerFactory());
        DefaultErrorHandler errorHandler = new DefaultErrorHandler();
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
