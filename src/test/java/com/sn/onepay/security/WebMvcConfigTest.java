package com.sn.onepay.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTest {

    @Mock
    AuditLoggingInterceptor auditLoggingInterceptor;

    @Mock
    InterceptorRegistry registry;

    @Mock
    InterceptorRegistration registration;

    @InjectMocks
    WebMvcConfig webMvcConfig;

    @Test
    void addInterceptors_registersAuditLoggingInterceptorOnOnepayPaths() {
        when(registry.addInterceptor(auditLoggingInterceptor)).thenReturn(registration);
        when(registration.addPathPatterns("/v1/onepay/**")).thenReturn(registration);

        webMvcConfig.addInterceptors(registry);

        verify(registry).addInterceptor(auditLoggingInterceptor);
        verify(registration).addPathPatterns("/v1/onepay/**");
    }
}
