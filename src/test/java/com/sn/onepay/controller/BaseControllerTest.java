package com.sn.onepay.controller;

import com.sn.onepay.services.AuditLogService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/*Each subclass disables security filters via @AutoConfigureMockMvc(addFilters = false): authorization rules are covered by SecurityConfigTest*/
abstract class BaseControllerTest {

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    JwtDecoder jwtDecoder;

    /*AuditLoggingInterceptor is a @Component implementing HandlerInterceptor, and WebMvcConfig is a
      WebMvcConfigurer: @WebMvcTest auto-detects both regardless of which controller is sliced,
      so every slice needs this dependency mocked even though it never calls it directly*/
    @MockBean
    AuditLogService auditLogService;
}
