package io.github.leihuang96.wallet_service.config;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TracingInterceptor implements HandlerInterceptor {

    private final Tracer tracer;

    public TracingInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 创建新的 span，使用更多上下文信息
        var span = tracer.nextSpan()
                .name(request.getMethod() + " " + request.getRequestURI())
                .tag("http.method", request.getMethod())
                .tag("http.url", request.getRequestURI())
                .start();

        // 显式调用 tracer.withSpan 以确保 span 在当前上下文中可用
        tracer.withSpan(span);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        var currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            if (ex != null) {
                currentSpan.tag("error", ex.getMessage());
            }
            currentSpan.tag("http.status_code", String.valueOf(response.getStatus()));
            currentSpan.end();
        }
    }
}