package io.github.leihuang96.wallet_service.config;

import brave.Tracing;
import brave.handler.SpanHandler;
import brave.sampler.Sampler;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.okhttp3.OkHttpSender;

@Configuration
public class TracingConfig {

    @Bean
    public OkHttpSender sender() {
        return OkHttpSender.create("http://localhost:9411/api/v2/spans");
    }

    @Bean
    public SpanHandler zipkinSpanHandler(OkHttpSender sender) {
        return AsyncZipkinSpanHandler.create(sender);
    }

    @Bean
    public Tracing braveTracing(SpanHandler spanHandler) {
        return Tracing.newBuilder()
                .localServiceName("wallet-service")
                .sampler(Sampler.ALWAYS_SAMPLE)
                .addSpanHandler(spanHandler)
                .supportsJoin(false)
                .traceId128Bit(true)
                .build();
    }

    @Bean
    public brave.Tracer braveTracer(Tracing braveTracing) {
        return braveTracing.tracer();
    }

    @Bean
    public Tracer tracer(brave.Tracer braveTracer, Tracing braveTracing) {
        return new BraveTracer(
                braveTracer,
                new BraveCurrentTraceContext(braveTracing.currentTraceContext()),
                new BraveBaggageManager()
        );
    }
}