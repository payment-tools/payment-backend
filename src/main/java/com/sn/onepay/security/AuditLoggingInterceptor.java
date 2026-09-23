package com.sn.onepay.security;

import com.sn.onepay.enumeration.AuditSeverity;
import com.sn.onepay.services.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.LinkedHashMap;
import java.util.Map;

/*Best-effort audit trail of mutating (POST/PUT/DELETE) calls to sensitive admin resources.
  Never allowed to affect the real request: every failure is caught and logged, not propagated.
  Login/logout events are NOT captured here - they happen at Keycloak, invisible to this resource server.*/
@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuditLoggingInterceptor implements HandlerInterceptor {

    static final Map<String, String> SENSITIVE_RESOURCE_LABELS = new LinkedHashMap<>();

    static {
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/client", "Client");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/cashier", "Caissier");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/enterpriseProfile", "Profil entreprise");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/salesProfile", "Profil commerce");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/enterpriseConfiguration", "Configuration entreprise");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/salesConfiguration", "Configuration commerce");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/enterprise", "Entreprise");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/sales", "Commerce");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/partnership", "Partenariat");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/subvention", "Subvention");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/employee-group", "Groupe de collaborateurs");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/bills", "Facture");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/prospect", "Prospect");
        SENSITIVE_RESOURCE_LABELS.put("/v1/onepay/payment", "Paiement");
    }

    final AuditLogService auditLogService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        try {
            String method = request.getMethod();
            if (!isMutating(method)) {
                return;
            }

            String path = request.getRequestURI();
            String matchedPrefix = matchSensitivePrefix(path);
            if (matchedPrefix == null) {
                return;
            }

            /*Routine cashier/client payment creation is high-frequency, not an admin action - excluded, unlike PUT/DELETE (cancellation)*/
            if (matchedPrefix.equals("/v1/onepay/payment") && "POST".equalsIgnoreCase(method)) {
                return;
            }

            int status = response.getStatus();
            if (status < 200 || status >= 300) {
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String eventType = verbFor(method) + " " + SENSITIVE_RESOURCE_LABELS.get(matchedPrefix);

            auditLogService.recordEvent(eventType, method, path, extractUsername(authentication), extractRole(authentication),
                    request.getRemoteAddr(), AuditSeverity.INFO, null);

        } catch (Exception e) {
            log.error("Failed to record audit event for {} {}", request.getMethod(), request.getRequestURI(), e);
        }
    }

    private boolean isMutating(String method) {
        return "POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method);
    }

    private String matchSensitivePrefix(String path) {
        for (String prefix : SENSITIVE_RESOURCE_LABELS.keySet()) {
            if (path.equals(prefix) || path.startsWith(prefix + "/")) {
                return prefix;
            }
        }
        return null;
    }

    private String verbFor(String method) {
        if ("POST".equalsIgnoreCase(method)) {
            return "Creation";
        }
        if ("PUT".equalsIgnoreCase(method)) {
            return "Modification";
        }
        return "Suppression";
    }

    private String extractUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("preferred_username");
        }
        return authentication.getName();
    }

    private String extractRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities().isEmpty()) {
            return null;
        }
        String authority = authentication.getAuthorities().iterator().next().getAuthority();
        return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
    }
}
