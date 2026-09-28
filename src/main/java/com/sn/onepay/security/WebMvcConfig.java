package com.sn.onepay.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    final AuditLoggingInterceptor auditLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditLoggingInterceptor).addPathPatterns("/v1/onepay/**");
    }
}
