package com.sn.onepay.controller;

import com.sn.onepay.dto.DeviceTokenDTO;
import com.sn.onepay.dto.NotificationDTO;
import com.sn.onepay.enumeration.DevicePlatform;
import com.sn.onepay.enumeration.NotificationType;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.services.NotificationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest extends BaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    NotificationService notificationService;

    /*Filtres de sécurité désactivés dans ce slice : on place directement l'utilisateur dans le
      SecurityContext, lu par @AuthenticationPrincipal (MockMvc s'exécute dans le même thread)*/
    static RequestPostProcessor user(String username) {
        return request -> {
            Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("preferred_username", username).build();
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
            return request;
        };
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerDeviceToken_usesTheConnectedUser() throws Exception {
        mockMvc.perform(post("/v1/onepay/notification/device-token").with(user("client.test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"fcm-1\",\"platform\":\"ANDROID\"}"))
                .andExpect(status().isNoContent());

        verify(notificationService).registerDeviceToken("client.test", new DeviceTokenDTO("fcm-1", DevicePlatform.ANDROID));
    }

    @Test
    void registerDeviceToken_returns400WithoutToken() throws Exception {
        mockMvc.perform(post("/v1/onepay/notification/device-token").with(user("client.test"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"platform\":\"ANDROID\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(notificationService);
    }

    @Test
    void unregisterDeviceToken_usesTheConnectedUser() throws Exception {
        mockMvc.perform(delete("/v1/onepay/notification/device-token/fcm-1").with(user("client.test")))
                .andExpect(status().isNoContent());

        verify(notificationService).unregisterDeviceToken("client.test", "fcm-1");
    }

    @Test
    void getMyNotifications_mostRecentFirst() throws Exception {
        var dto = new NotificationDTO(1L, NotificationType.PAYMENT_CREATED, "Paiement effectué", "Paiement de 2 500 FCFA chez Auchan", 7L, false, null);
        when(notificationService.getMyNotifications(eq("client.test"), eq(false), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/v1/onepay/notification").param("read", "false").with(user("client.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Paiement effectué"));

        verify(notificationService).getMyNotifications(eq("client.test"), eq(false),
                argThat(pageable -> Sort.Direction.DESC == pageable.getSort().getOrderFor("creationDate").getDirection()));
    }

    @Test
    void countUnread_ofTheConnectedUser() throws Exception {
        when(notificationService.countUnread("cashier.test")).thenReturn(3L);

        mockMvc.perform(get("/v1/onepay/notification/unread-count").with(user("cashier.test")))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void markAsRead_returns404ForNotificationOfAnotherUser() throws Exception {
        when(notificationService.markAsRead(5L, "cashier.test")).thenThrow(new ResourceNotFoundException("Notification", "ID", 5L));

        mockMvc.perform(put("/v1/onepay/notification/5/read").with(user("cashier.test")))
                .andExpect(status().isNotFound());
    }

    @Test
    void markAsRead_returnsTheNotification() throws Exception {
        when(notificationService.markAsRead(5L, "client.test"))
                .thenReturn(new NotificationDTO(5L, NotificationType.PAYMENT_CANCELLED, "Paiement annulé", "m", 7L, true, null));

        mockMvc.perform(put("/v1/onepay/notification/5/read").with(user("client.test")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void markAllAsRead_ofTheConnectedUser() throws Exception {
        when(notificationService.markAllAsRead("client.test")).thenReturn(4);

        mockMvc.perform(put("/v1/onepay/notification/read-all").with(user("client.test")))
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }

    @Test
    void withoutAuthentication_usernameIsNull() throws Exception {
        when(notificationService.countUnread(null)).thenReturn(0L);

        mockMvc.perform(get("/v1/onepay/notification/unread-count"))
                .andExpect(status().isOk());

        verify(notificationService).countUnread(null);
    }
}
