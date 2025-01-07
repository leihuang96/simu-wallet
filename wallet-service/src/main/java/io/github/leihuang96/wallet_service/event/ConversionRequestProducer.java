package io.github.leihuang96.wallet_service.event;

import io.github.leihuang96.common_module.TransactionEvent;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// 用户发起金额转换请求，将请求发送到 Kafka 主题 `conversion-request`
@Slf4j
@Component
public class ConversionRequestProducer {

    private static final Logger logger = LoggerFactory.getLogger(ConversionRequestProducer.class);
    private static final String CONVERSION_REQUEST_TOPIC = "conversion-request";
    private final Tracer tracer;

    @Autowired
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public ConversionRequestProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }
    /**
     * 发送金额转换请求到 Kafka
     */
    public void sendConversionRequest(TransactionEvent transactionEvent) {
        var span = tracer.nextSpan().name("send-conversion-request").start();
        try (var ignored = tracer.withSpan(span)) {
            String traceId = span.context().traceId();
            String spanId = span.context().spanId();

            transactionEvent.setTraceId(traceId);
            transactionEvent.setSpanId(spanId);

            validateInputs(transactionEvent);

            kafkaTemplate.send(CONVERSION_REQUEST_TOPIC, transactionEvent);
            log.info("Sent conversion request to Kafka: {}", transactionEvent);
        } catch (Exception e) {
            span.tag("error", e.getMessage());
            log.error("Error sending conversion request: {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            span.end();
        }
    }

    private void validateInputs(TransactionEvent transactionEvent) {
        if (transactionEvent == null) {
            throw new IllegalArgumentException("TransactionEvent cannot be null");
        }
    }
}
