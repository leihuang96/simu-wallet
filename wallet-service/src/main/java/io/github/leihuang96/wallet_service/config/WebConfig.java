package io.github.leihuang96.wallet_service.config;

import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final Tracer tracer;

    public WebConfig(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TracingInterceptor(tracer))
                .addPathPatterns("/**"); // 确保拦截所有路径
    }
}

