package io.github.leihuang96.wallet_service.config;

import io.micrometer.tracing.Tracer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private static final Logger logger = LoggerFactory.getLogger(TracingAspect.class);

    private final Tracer tracer;

    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    // Pointcut for all methods in Controller
    @Pointcut("within(io.github.leihuang96.wallet_service.controller..*)")
    public void controllerMethods() {
    }

    // Pointcut for all methods in Application Services
    @Pointcut("within(io.github.leihuang96.wallet_service.application..*)")
    public void applicationServiceMethods() {
    }

    // Around advice for tracing method execution
    @Around("controllerMethods() || applicationServiceMethods()")
    public Object logExecutionTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = tracer.currentSpan() != null ? tracer.currentSpan()
                .context()
                .traceId() : "N/A";
        String spanId = tracer.currentSpan() != null ? tracer.currentSpan()
                .context()
                .spanId() : "N/A";

        logger.info(
                "Entering method: {} with traceId={} and spanId={}",
                joinPoint.getSignature()
                        .toShortString(), traceId, spanId
        );

        try {
            Object result = joinPoint.proceed();
            logger.info(
                    "Exiting method: {} with traceId={} and spanId={}",
                    joinPoint.getSignature()
                            .toShortString(), traceId, spanId
            );
            return result;
        }
        catch (Throwable throwable) {
            logger.error(
                    "Exception in method: {} with traceId={} and spanId={}",
                    joinPoint.getSignature()
                            .toShortString(), traceId, spanId, throwable
            );
            throw throwable;
        }
    }

    // After throwing advice for exception logging
    @AfterThrowing(pointcut = "controllerMethods() || applicationServiceMethods()", throwing = "ex")
    public void logException(Throwable ex) {
        String traceId = tracer.currentSpan() != null ? tracer.currentSpan()
                .context()
                .traceId() : "N/A";
        String spanId = tracer.currentSpan() != null ? tracer.currentSpan()
                .context()
                .spanId() : "N/A";

        logger.error("Exception caught with traceId={} and spanId={}: {}", traceId, spanId, ex.getMessage());
    }
}
