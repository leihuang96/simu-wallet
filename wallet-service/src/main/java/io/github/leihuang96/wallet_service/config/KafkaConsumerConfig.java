package io.github.leihuang96.wallet_service.config;

import io.github.leihuang96.common_module.ConversionResponseEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(KafkaConsumerConfig.class);
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> commonConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "wallet-service-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "io.github.leihuang96.common_module");

        // 添加追踪拦截器
        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG,
                TracingConsumerInterceptor.class.getName());

        return props;
    }

    @Bean
    public ConsumerFactory<String, ConversionResponseEvent> conversionResponseConsumerFactory() {
        logger.info("Creating Kafka Consumer Factory for ConversionResponseEvent");
        return new DefaultKafkaConsumerFactory<>(
                commonConsumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(ConversionResponseEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConversionResponseEvent> conversionResponseContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConversionResponseEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(conversionResponseConsumerFactory());
        DefaultErrorHandler errorHandler = new DefaultErrorHandler();
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

}
