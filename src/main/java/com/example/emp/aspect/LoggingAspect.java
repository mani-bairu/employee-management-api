package com.example.emp.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllers() {
    }

    @Around("restControllers()")
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = "N/A";
        String uri = "N/A";
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            method = request.getMethod();
            uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        }
        String handler = joinPoint.getSignature().getDeclaringType().getSimpleName()
                + "." + joinPoint.getSignature().getName();

        log.info("REQUEST  -> {} {} | handler={} | args={}", method, uri, handler, Arrays.toString(joinPoint.getArgs()));
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            log.info("RESPONSE <- {} {} | {} ms | body={}", method, uri, System.currentTimeMillis() - start, result);
            return result;
        } catch (Throwable ex) {
            log.warn("RESPONSE <- {} {} | {} ms | exception={}: {}", method, uri,
                    System.currentTimeMillis() - start, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}
