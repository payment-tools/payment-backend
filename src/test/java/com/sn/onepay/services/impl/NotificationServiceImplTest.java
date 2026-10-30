package com.sn.onepay.services.impl;

import com.sn.onepay.dto.DeviceTokenDTO;
import com.sn.onepay.dto.NotificationDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.DeviceToken;
import com.sn.onepay.entity.Enterprise;
import com.sn.onepay.entity.Notification;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.entity.Sales;
import com.sn.onepay.enumeration.DevicePlatform;
import com.sn.onepay.enumeration.NotificationType;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.NotificationMapper;
import com.sn.onepay.notification.PaymentNotificationEvent;
import com.sn.onepay.notification.PushMessage;
import com.sn.onepay.notification.PushSender;
import com.sn.onepay.repository.DeviceTokenRepository;
import com.sn.onepay.repository.NotificationRepository;
import com.sn.onepay.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    static final String NBSP = " ";

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    DeviceTokenRepository deviceTokenRepository;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    NotificationMapper notificationMapper;

    @Mock
    PushSender pushSender;

    @InjectMocks
    NotificationServiceImpl notificationService;

    @Test
    void onPaymentCreated_notifiesClientAndCashierAndPushesToTheirDevices() {
        when(paymentRepository.findById(7L)).thenReturn(Optional.of(payment(2500.0)));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification notification = invocation.getArgument(0);
            notification.setId(notification.getRecipientUsername().equals("client.test") ? 100L : 101L);
            return notification;
        });
        when(deviceTokenRepository.findByUsername("client.test")).thenReturn(List.of(token("client-phone")));
        when(deviceTokenRepository.findByUsername("cashier.test")).thenReturn(List.of(token("cashier-phone"), token("old-phone")));
        when(pushSender.send(eq(List.of("client-phone")), any(PushMessage.class))).thenReturn(List.of());
        when(pushSender.send(eq(List.of("cashier-phone", "old-phone")), any(PushMessage.class))).thenReturn(List.of("old-phone"));

        notificationService.onPaymentEvent(new PaymentNotificationEvent(7L, NotificationType.PAYMENT_CREATED));

        ArgumentCaptor<Notification> saved = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(saved.capture());
        assertThat(saved.getAllValues()).extracting(Notification::getRecipientUsername, Notification::getTitle, Notification::getMessage)
                .containsExactly(
                        tuple("client.test", "Paiement effectué", "Paiement de 2" + NBSP + "500" + NBSP + "FCFA chez Auchan Mermoz"),
                        tuple("cashier.test", "Paiement reçu", "Paiement reçu : 2" + NBSP + "500" + NBSP + "FCFA de Awa Ndiaye (Sonatel)"));
        assertThat(saved.getAllValues()).allSatisfy(notification -> {
            assertThat(notification.getType()).isEqualTo(NotificationType.PAYMENT_CREATED);
            assertThat(notification.getPaymentId()).isEqualTo(7L);
            assertThat(notification.isRead()).isFalse();
        });

        ArgumentCaptor<PushMessage> push = ArgumentCaptor.forClass(PushMessage.class);
        verify(pushSender, times(2)).send(any(), push.capture());
        assertThat(push.getAllValues().get(0)).isEqualTo(new PushMessage("Paiement effectué", "Paiement de 2" + NBSP + "500" + NBSP + "FCFA chez Auchan Mermoz",
                Map.of("type", "PAYMENT_CREATED", "paymentId", "7", "notificationId", "100")));
        verify(deviceTokenRepository).deleteByTokenIn(List.of("old-phone"));
    }

    @Test
    void onPaymentCancelled_notifiesBothWithCancellationTexts_withoutPushWhenNoDevice() {
        when(paymentRepository.findById(7L)).thenReturn(Optional.of(payment(12000.0)));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(deviceTokenRepository.findByUsername(any())).thenReturn(List.of());

        notificationService.onPaymentEvent(new PaymentNotificationEvent(7L, NotificationType.PAYMENT_CANCELLED));

        ArgumentCaptor<Notification> saved = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(saved.capture());
        assertThat(saved.getAllValues()).extracting(Notification::getTitle, Notification::getMessage).containsExactly(
                tuple("Paiement annulé", "Paiement de 12" + NBSP + "000" + NBSP + "FCFA chez Auchan Mermoz annulé"),
                tuple("Paiement annulé", "Paiement de 12" + NBSP + "000" + NBSP + "FCFA de Awa Ndiaye (Sonatel) annulé"));
        verifyNoInteractions(pushSender);
        verify(deviceTokenRepository, never()).deleteByTokenIn(any());
    }

    @Test
    void onPaymentEvent_pushFailureDoesNotPreventTheOtherNotification() {
        when(paymentRepository.findById(7L)).thenReturn(Optional.of(payment(2500.0)));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(deviceTokenRepository.findByUsername(any())).thenReturn(List.of(token("phone")));
        when(pushSender.send(any(), any())).thenThrow(new IllegalStateException("push down"));

        notificationService.onPaymentEvent(new PaymentNotificationEvent(7L, NotificationType.PAYMENT_CREATED));

        verify(notificationRepository, times(2)).save(any(Notification.class));
        verify(pushSender, times(2)).send(any(), any());
    }

    @Test
    void onPaymentEvent_neverThrows() {
        when(paymentRepository.findById(7L)).thenReturn(Optional.empty());

        notificationService.onPaymentEvent(new PaymentNotificationEvent(7L, NotificationType.PAYMENT_CREATED));

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void formatAmount_groupsThousandsAndRounds() {
        assertThat(NotificationServiceImpl.formatAmount(1234567.6)).isEqualTo("1" + NBSP + "234" + NBSP + "568" + NBSP + "FCFA");
        assertThat(NotificationServiceImpl.formatAmount(500.0)).isEqualTo("500" + NBSP + "FCFA");
    }

    @Test
    void getMyNotifications_allOrFilteredByReadStatus() {
        var notification = new Notification();
        var dto = mock(NotificationDTO.class);
        when(notificationMapper.asDTO(notification)).thenReturn(dto);
        when(notificationRepository.findByRecipientUsername("client.test", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(notification)));
        when(notificationRepository.findByRecipientUsernameAndRead("client.test", false, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(notification)));

        assertThat(notificationService.getMyNotifications("client.test", null, Pageable.unpaged()).getContent()).containsExactly(dto);
        assertThat(notificationService.getMyNotifications("client.test", false, Pageable.unpaged()).getContent()).containsExactly(dto);
    }

    @Test
    void countUnread_countsOnlyTheUsersUnreadNotifications() {
        when(notificationRepository.countByRecipientUsernameAndReadFalse("client.test")).thenReturn(3L);

        assertThat(notificationService.countUnread("client.test")).isEqualTo(3L);
    }

    @Test
    void markAsRead_marksOwnNotification() {
        var notification = new Notification();
        var dto = mock(NotificationDTO.class);
        when(notificationRepository.findByIdAndRecipientUsername(5L, "client.test")).thenReturn(Optional.of(notification));
        when(notificationRepository.save(notification)).thenReturn(notification);
        when(notificationMapper.asDTO(notification)).thenReturn(dto);

        assertThat(notificationService.markAsRead(5L, "client.test")).isEqualTo(dto);
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void markAsRead_notificationOfAnotherUserIsNotFound() {
        when(notificationRepository.findByIdAndRecipientUsername(5L, "cashier.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(5L, "cashier.test")).isInstanceOf(ResourceNotFoundException.class);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void markAllAsRead_onlyForTheUser() {
        when(notificationRepository.markAllAsRead("client.test")).thenReturn(4);

        assertThat(notificationService.markAllAsRead("client.test")).isEqualTo(4);
    }

    @Test
    void registerDeviceToken_createsNewToken() {
        when(deviceTokenRepository.findByToken("fcm-1")).thenReturn(Optional.empty());

        notificationService.registerDeviceToken("client.test", new DeviceTokenDTO("fcm-1", DevicePlatform.ANDROID));

        ArgumentCaptor<DeviceToken> saved = ArgumentCaptor.forClass(DeviceToken.class);
        verify(deviceTokenRepository).save(saved.capture());
        assertThat(saved.getValue().getToken()).isEqualTo("fcm-1");
        assertThat(saved.getValue().getUsername()).isEqualTo("client.test");
        assertThat(saved.getValue().getPlatform()).isEqualTo(DevicePlatform.ANDROID);
    }

    @Test
    void registerDeviceToken_reattachesExistingTokenToTheConnectedUser() {
        var existing = token("fcm-1");
        existing.setId(3L);
        existing.setUsername("cashier.test");
        when(deviceTokenRepository.findByToken("fcm-1")).thenReturn(Optional.of(existing));

        notificationService.registerDeviceToken("client.test", new DeviceTokenDTO("fcm-1", DevicePlatform.IOS));

        verify(deviceTokenRepository).save(existing);
        assertThat(existing.getUsername()).isEqualTo("client.test");
        assertThat(existing.getPlatform()).isEqualTo(DevicePlatform.IOS);
    }

    @Test
    void unregisterDeviceToken_onlyRemovesTheUsersToken() {
        notificationService.unregisterDeviceToken("client.test", "fcm-1");

        verify(deviceTokenRepository).deleteByTokenAndUsername("fcm-1", "client.test");
    }

    private static DeviceToken token(String value) {
        var token = new DeviceToken();
        token.setToken(value);
        token.setPlatform(DevicePlatform.ANDROID);
        return token;
    }

    private static Payment payment(double amount) {
        var enterprise = new Enterprise();
        enterprise.setName("Sonatel");
        var client = new Client();
        client.setUsername("client.test");
        client.setFirstname("Awa");
        client.setLastname("Ndiaye");
        client.setEnterprise(enterprise);
        var sales = new Sales();
        sales.setName("Auchan Mermoz");
        var cashier = new Cashier();
        cashier.setUsername("cashier.test");
        cashier.setSales(sales);
        var payment = new Payment();
        payment.setId(7L);
        payment.setAmount(amount);
        payment.setClient(client);
        payment.setCashier(cashier);
        return payment;
    }
}
