package io.github.leihuang96.wallet_service.config;

import io.github.leihuang96.common_module.ConversionResponseEvent;
import io.micrometer.tracing.Tracer;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@Component
public class TracingConsumerInterceptor implements ConsumerInterceptor<String, ConversionResponseEvent> {
    private static final Logger log = LoggerFactory.getLogger(TracingConsumerInterceptor.class);
    private Tracer tracer;

    // 无参构造函数，Kafka 需要
    public TracingConsumerInterceptor() {
    }

    @Override
    public ConsumerRecords<String, ConversionResponseEvent> onConsume(ConsumerRecords<String, ConversionResponseEvent> records) {
        if (tracer == null) {
            log.warn("Tracer is not initialized");
            return records;
        }

        for (ConsumerRecord<String, ConversionResponseEvent> record : records) {
            ConversionResponseEvent event = record.value();
            if (event != null) {
                var spanBuilder = tracer.nextSpan()
                        .name("kafka-consume-" + record.topic())
                        .tag("kafka.topic", record.topic());

                if (event.getTraceId() != null) {
                    spanBuilder.tag("originalTraceId", event.getTraceId());
                }

                spanBuilder.start();
            }
        }
        return records;
    }

    @Override
    public void onCommit(Map<org.apache.kafka.common.TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata> offsets) {
        if (tracer != null && tracer.currentSpan() != null) {
            tracer.currentSpan().end();
        }
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // 从 Spring 上下文获取 Tracer
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        if (applicationContext != null) {
            this.tracer = applicationContext.getBean(Tracer.class);
        } else {
            log.error("Failed to get ApplicationContext");
        }
    }

    @Override
    public void close() {
        // 清理资源如果需要
    }
}