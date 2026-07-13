package com.sn.onepay.security;

import com.sn.onepay.controller.PaymentController;
import com.sn.onepay.dto.PaymentDTO;
import com.sn.onepay.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    static final String PAYMENT_BODY = "{\"client\":{\"id\":1},\"cashier\":{\"id\":2},\"amount\":50.0,\"status\":\"ACTIVE\",\"module\":\"RESTAURATION\"}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PaymentService paymentService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    JwtDecoder jwtDecoder;

    private static RequestPostProcessor as(String role) {
        return jwt().authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    /*Routes without a loaded controller pass security then return 404: any status except 401/403 proves access was granted*/
    private static void assertAccessGranted(ResultActions actions) throws Exception {
        int status = actions.andReturn().getResponse().getStatus();
        assertThat(status).isNotIn(401, 403);
    }

    @Test
    void anyRequest_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/v1/onepay/payment"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void swagger_isAccessibleWithoutToken() throws Exception {
        assertAccessGranted(mockMvc.perform(get("/v3/api-docs")));
        assertAccessGranted(mockMvc.perform(get("/swagger-ui/index.html")));
    }

    @Test
    void enterpriseWrites_requireSuperAdmin() throws Exception {
        mockMvc.perform(post("/v1/onepay/enterprise").with(as("ENTERPRISE_ADMIN"))).andExpect(status().isForbidden());
        mockMvc.perform(put("/v1/onepay/enterprise/1").with(as("CLIENT"))).andExpect(status().isForbidden());
        mockMvc.perform(delete("/v1/onepay/enterprise/1").with(as("SALES_ADMIN"))).andExpect(status().isForbidden());
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/enterprise").with(as("SUPER_ADMIN"))));
    }

    @Test
    void salesAndPartnershipWrites_requireSuperAdmin() throws Exception {
        mockMvc.perform(post("/v1/onepay/sales").with(as("SALES_ADMIN"))).andExpect(status().isForbidden());
        mockMvc.perform(delete("/v1/onepay/partnership/1").with(as("ENTERPRISE_ADMIN"))).andExpect(status().isForbidden());
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/sales").with(as("SUPER_ADMIN"))));
        assertAccessGranted(mockMvc.perform(delete("/v1/onepay/partnership/1").with(as("SUPER_ADMIN"))));
    }

    @Test
    void enterpriseSideWrites_requireEnterpriseAdmin() throws Exception {
        mockMvc.perform(post("/v1/onepay/client").with(as("CASHIER"))).andExpect(status().isForbidden());
        mockMvc.perform(post("/v1/onepay/subvention").with(as("SALES_ADMIN"))).andExpect(status().isForbidden());
        mockMvc.perform(put("/v1/onepay/employee-group/1").with(as("ENTERPRISE_FINANCE"))).andExpect(status().isForbidden());
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/client").with(as("ENTERPRISE_ADMIN"))));
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/subvention").with(as("ENTERPRISE_ADMIN"))));
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/enterprise-profile").with(as("ENTERPRISE_ADMIN"))));
        assertAccessGranted(mockMvc.perform(put("/v1/onepay/enterprise-configuration/1").with(as("SUPER_ADMIN"))));
    }

    @Test
    void salesSideWrites_requireSalesAdmin() throws Exception {
        mockMvc.perform(post("/v1/onepay/cashier").with(as("ENTERPRISE_ADMIN"))).andExpect(status().isForbidden());
        mockMvc.perform(put("/v1/onepay/sales-configuration/1").with(as("CLIENT"))).andExpect(status().isForbidden());
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/cashier").with(as("SALES_ADMIN"))));
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/sales-profile").with(as("SALES_ADMIN"))));
        assertAccessGranted(mockMvc.perform(put("/v1/onepay/sales-configuration/1").with(as("SUPER_ADMIN"))));
    }

    @Test
    void paymentCreation_isAllowedForClientAndCashier() throws Exception {
        when(paymentService.createPayment(any())).thenReturn(mock(PaymentDTO.class));

        mockMvc.perform(post("/v1/onepay/payment").with(as("ENTERPRISE_FINANCE"))
                        .contentType(MediaType.APPLICATION_JSON).content(PAYMENT_BODY))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/v1/onepay/payment").with(as("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON).content(PAYMENT_BODY))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/v1/onepay/payment").with(as("CASHIER"))
                        .contentType(MediaType.APPLICATION_JSON).content(PAYMENT_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void paymentUpdateAndCancellation_requireCashier() throws Exception {
        when(paymentService.updatePayment(any(), anyLong())).thenReturn(mock(PaymentDTO.class));
        doNothing().when(paymentService).deletePayment(anyLong());

        mockMvc.perform(delete("/v1/onepay/payment/1").with(as("CLIENT"))).andExpect(status().isForbidden());
        mockMvc.perform(put("/v1/onepay/payment/1").with(as("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON).content(PAYMENT_BODY))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/v1/onepay/payment/1").with(as("CASHIER"))).andExpect(status().isOk());
        mockMvc.perform(put("/v1/onepay/payment/1").with(as("CASHIER"))
                        .contentType(MediaType.APPLICATION_JSON).content(PAYMENT_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void billsWrites_requireFinanceOrSuperAdminRoles() throws Exception {
        mockMvc.perform(post("/v1/onepay/bills").with(as("CLIENT"))).andExpect(status().isForbidden());
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/bills").with(as("ENTERPRISE_FINANCE"))));
        assertAccessGranted(mockMvc.perform(post("/v1/onepay/bills").with(as("SALES_FINANCE"))));
        assertAccessGranted(mockMvc.perform(put("/v1/onepay/bills/1").with(as("SUPER_ADMIN"))));
    }

    @Test
    void reads_areAllowedForAnyAuthenticatedRole() throws Exception {
        when(paymentService.getPaymentByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/payment").with(as("ENTERPRISE_FINANCE"))).andExpect(status().isOk());
        assertAccessGranted(mockMvc.perform(get("/v1/onepay/enterprise").with(as("CASHIER"))));
    }

    @Test
    void realmRolesFromJwtClaims_grantAccessThroughConverter() throws Exception {
        doNothing().when(paymentService).deletePayment(anyLong());

        mockMvc.perform(delete("/v1/onepay/payment/1")
                        .with(jwt()
                                .jwt(builder -> builder.claim("realm_access", Map.of("roles", List.of("CASHIER"))))
                                .authorities(new KeycloakRealmRoleConverter())))
                .andExpect(status().isOk());
    }
}
