package com.sn.onepay.controller;

import java.util.Set;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import com.sn.onepay.dto.BillDetailsDTO;
import com.sn.onepay.dto.BillsDTO;
import com.sn.onepay.enumeration.Modules;
import com.sn.onepay.enumeration.BillStatus;
import com.sn.onepay.services.BillsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BillsController.class)
@AutoConfigureMockMvc(addFilters = false)
class BillsControllerTest extends BaseControllerTest {

    static final String VALID_BODY = "{\"partnership\":{\"id\":1,\"sales\":{\"id\":1,\"type\":\"RESTAURATION\"},\"enterprise\":{\"id\":2,\"name\":\"Corp\",\"maxQuota\":100}}," +
            "\"startDate\":\"2026-07-01T00:00:00\",\"endDate\":\"2026-07-31T23:59:59\"," +
            "\"totalAmount\":5000.0,\"billStatus\":\"UNPAYED\",\"period\":\"2026-07\"}";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BillsService billsService;

    @Test
    void createBills_returns201() throws Exception {
        when(billsService.createBills(any())).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(post("/v1/onepay/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void createBills_returns400WhenBodyInvalid() throws Exception {
        mockMvc.perform(post("/v1/onepay/bills")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBills_returns200() throws Exception {
        when(billsService.updateBills(any(), anyLong())).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(put("/v1/onepay/bills/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void getBillsByFilters_returns200() throws Exception {
        when(billsService.getBillsByFilters(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/v1/onepay/bills"))
                .andExpect(status().isOk());
    }

    @Test
    void getBillsById_returns200() throws Exception {
        when(billsService.getBillsById(1L)).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(get("/v1/onepay/bills/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBills_returns200() throws Exception {
        doNothing().when(billsService).deleteBills(anyLong());

        mockMvc.perform(delete("/v1/onepay/bills/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getBillDetails_returns200() throws Exception {
        when(billsService.getBillDetails(1L)).thenReturn(new BillDetailsDTO("REF-1", "2026-10", null, null,
                BillStatus.UNPAYED, 12500.0, new BillDetailsDTO.EnterpriseInfo("Sonatel", "Dakar"),
                new BillDetailsDTO.SalesInfo("Auchan", "Mermoz", Modules.MARKET)));

        mockMvc.perform(get("/v1/onepay/bills/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enterprise.name").value("Sonatel"))
                .andExpect(jsonPath("$.sales.type").value("MARKET"))
                .andExpect(jsonPath("$.totalAmount").value(12500.0));
    }

    @Test
    void confirmPayment_passesUsernameAndRolesOfTheCaller() throws Exception {
        when(billsService.confirmPayment(eq(1L), eq("ent.finance"), eq(Set.of("ENTERPRISE_FINANCE")))).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(put("/v1/onepay/bills/1/payment-confirmation").principal(authentication("ent.finance", "ROLE_ENTERPRISE_FINANCE")))
                .andExpect(status().isOk());

        verify(billsService).confirmPayment(1L, "ent.finance", Set.of("ENTERPRISE_FINANCE"));
    }

    @Test
    void confirmPayment_returns403ForAnotherOrganisation() throws Exception {
        when(billsService.confirmPayment(eq(1L), any(), any()))
                .thenThrow(new AccessDeniedException("Seule la finance de l'entreprise ou du commerce de ce partenariat peut confirmer le paiement de cette facture"));

        mockMvc.perform(put("/v1/onepay/bills/1/payment-confirmation").principal(authentication("other", "ROLE_SALES_FINANCE")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Seule la finance de l'entreprise ou du commerce de ce partenariat peut confirmer le paiement de cette facture"));
    }

    @Test
    void cancelPaymentConfirmation_passesUsernameAndRolesOfTheCaller() throws Exception {
        when(billsService.cancelPaymentConfirmation(eq(1L), eq("sales.finance"), eq(Set.of("SALES_FINANCE")))).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(delete("/v1/onepay/bills/1/payment-confirmation").principal(authentication("sales.finance", "ROLE_SALES_FINANCE")))
                .andExpect(status().isOk());

        verify(billsService).cancelPaymentConfirmation(1L, "sales.finance", Set.of("SALES_FINANCE"));
    }

    @Test
    void confirmPayment_withoutAuthentication_hasNoUserNorRole() throws Exception {
        when(billsService.confirmPayment(1L, null, Set.of())).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(put("/v1/onepay/bills/1/payment-confirmation"))
                .andExpect(status().isOk());

        verify(billsService).confirmPayment(1L, null, Set.of());
    }

    @Test
    void confirmPayment_principalWithoutJwt_hasNoUsername() throws Exception {
        when(billsService.confirmPayment(eq(1L), eq(null), any())).thenReturn(mock(BillsDTO.class));

        mockMvc.perform(put("/v1/onepay/bills/1/payment-confirmation")
                        .principal(new UsernamePasswordAuthenticationToken("someone", "n/a", List.of(new SimpleGrantedAuthority("ROLE_SALES_FINANCE")))))
                .andExpect(status().isOk());

        verify(billsService).confirmPayment(1L, null, Set.of("SALES_FINANCE"));
    }

    private static JwtAuthenticationToken authentication(String username, String role) {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("preferred_username", username).build();
        return new JwtAuthenticationToken(jwt, List.of(new SimpleGrantedAuthority(role)));
    }
}
