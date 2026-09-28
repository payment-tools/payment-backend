package com.sn.onepay.security;

import com.sn.onepay.enumeration.AuditSeverity;
import com.sn.onepay.services.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLoggingInterceptorTest {

    @Mock
    AuditLogService auditLogService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    AuditLoggingInterceptor interceptor;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAsJwt(String username, String role) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("preferred_username", username)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var authentication = new UsernamePasswordAuthenticationToken(jwt, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void firesOnMutatingWhitelistedPathWith2xxStatus() {
        authenticateAsJwt("jdoe", "SUPER_ADMIN");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(201);

        interceptor.afterCompletion(request, response, new Object(), null);

        ArgumentCaptor<String> eventTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(auditLogService).recordEvent(eventTypeCaptor.capture(), eq("POST"), eq("/v1/onepay/client"),
                eq("jdoe"), eq("SUPER_ADMIN"), eq("127.0.0.1"), eq(AuditSeverity.INFO), isNull());
        assertThat(eventTypeCaptor.getValue()).isEqualTo("Creation Client");
    }

    @Test
    void doesNotFireOnGet() {
        when(request.getMethod()).thenReturn("GET");

        interceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(auditLogService);
    }

    @Test
    void doesNotFireOnNon2xxStatus() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(response.getStatus()).thenReturn(400);

        interceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(auditLogService);
    }

    @Test
    void doesNotFireOnInformationalStatus() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(response.getStatus()).thenReturn(100);

        interceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(auditLogService);
    }

    @Test
    void doesNotFireOnNonWhitelistedPath() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/qrcode");

        interceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(auditLogService);
    }

    @Test
    void doesNotFireOnPaymentCreation() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/payment");

        interceptor.afterCompletion(request, response, new Object(), null);

        verifyNoInteractions(auditLogService);
    }

    @Test
    void firesOnPaymentCancellation() {
        authenticateAsJwt("cashier1", "CASHIER");
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/v1/onepay/payment/5");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).recordEvent(eq("Suppression Paiement"), eq("DELETE"), eq("/v1/onepay/payment/5"),
                eq("cashier1"), eq("CASHIER"), eq("127.0.0.1"), eq(AuditSeverity.INFO), isNull());
    }

    @Test
    void doesNotConfuseEnterpriseProfileWithEnterprisePrefix() {
        authenticateAsJwt("jdoe", "SUPER_ADMIN");
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/v1/onepay/enterpriseProfile/5");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(200);

        interceptor.afterCompletion(request, response, new Object(), null);

        ArgumentCaptor<String> eventTypeCaptor = ArgumentCaptor.forClass(String.class);
        verify(auditLogService).recordEvent(eventTypeCaptor.capture(), any(), any(), any(), any(), any(), any(), any());
        assertThat(eventTypeCaptor.getValue()).isEqualTo("Modification Profil entreprise");
    }

    @Test
    void swallowsExceptionFromRecordEventWithoutPropagating() {
        authenticateAsJwt("jdoe", "SUPER_ADMIN");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(201);
        doThrow(new RuntimeException("db down")).when(auditLogService)
                .recordEvent(any(), any(), any(), any(), any(), any(), any(), any());

        assertThatCode(() -> interceptor.afterCompletion(request, response, new Object(), null))
                .doesNotThrowAnyException();
    }

    @Test
    void extractsUsernameFromNonJwtPrincipalViaAuthenticationName() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("plainuser", null, "ROLE_SUPER_ADMIN"));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(201);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).recordEvent(any(), any(), any(), eq("plainuser"), eq("SUPER_ADMIN"), any(), any(), any());
    }

    @Test
    void actorRoleIsNullWhenAuthoritiesEmpty() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("noroles", null, List.of()));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(201);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).recordEvent(any(), any(), any(), eq("noroles"), isNull(), any(), any(), any());
    }

    @Test
    void actorRoleKeepsRawAuthorityWhenNoRolePrefix() {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken("rawauth", null, List.of(new SimpleGrantedAuthority("SUPER_ADMIN"))));
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(201);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).recordEvent(any(), any(), any(), eq("rawauth"), eq("SUPER_ADMIN"), any(), any(), any());
    }

    @Test
    void handlesNoAuthenticationInContext() {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/v1/onepay/client");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getStatus()).thenReturn(201);

        interceptor.afterCompletion(request, response, new Object(), null);

        verify(auditLogService).recordEvent(any(), any(), any(), isNull(), isNull(), any(), any(), any());
    }
}
