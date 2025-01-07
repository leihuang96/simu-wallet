package io.github.leihuang96.wallet_service.event;

import io.micrometer.tracing.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import io.github.leihuang96.common_module.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TransactionEventPublisher {
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    private final Tracer tracer;
    private static final Logger logger = LoggerFactory.getLogger(TransactionEventPublisher.class);

    public TransactionEventPublisher(KafkaTemplate<String, TransactionEvent> kafkaTemplate, Tracer tracer) {
        this.kafkaTemplate = kafkaTemplate;
        this.tracer = tracer;
    }

    public void sendTransactionEvent(TransactionEvent event) {
        if (tracer.currentSpan() != null) {
            event.setTraceId(tracer.currentSpan().context().traceId());
            event.setSpanId(tracer.currentSpan().context().spanId());
        }
        kafkaTemplate.send("transaction-topic", event);
        logger.info("Sent transaction event: {}", event);
    }
}
