package io.github.leihuang96.wallet_service.config;

import io.github.leihuang96.common_module.TransactionEvent;
import io.micrometer.tracing.Tracer;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TracingProducerInterceptor implements ProducerInterceptor<String, TransactionEvent> {
    private static final Logger log = LoggerFactory.getLogger(TracingProducerInterceptor.class);

    private Tracer tracer;

    public TracingProducerInterceptor() {
    }

    @Override
    public void configure(Map<String, ?> configs) {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        if (applicationContext != null) {
            this.tracer = applicationContext.getBean(Tracer.class);
        }
    }

    @Override
    public ProducerRecord<String, TransactionEvent> onSend(ProducerRecord<String, TransactionEvent> record) {
        if (tracer == null) {
            log.warn("Tracer is not initialized");
            return record;
        }

        if (tracer.currentSpan() != null) {
            TransactionEvent event = record.value();
            event.setTraceId(tracer.currentSpan()
                    .context()
                    .traceId());
            event.setSpanId(tracer.currentSpan()
                    .context()
                    .spanId());

            tracer.nextSpan()
                    .name("kafka-produce-" + record.topic())
                    .tag("kafka.topic", record.topic())
                    .start();
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (tracer != null && tracer.currentSpan() != null) {
            if (exception != null) {
                tracer.currentSpan()
                        .tag("error", exception.getMessage());
            }
            tracer.currentSpan()
                    .end();
        }
    }

    @Override
    public void close() {
    }
}