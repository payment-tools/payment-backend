package com.sn.onepay.services;

import com.sn.onepay.dto.DeviceTokenDTO;
import com.sn.onepay.dto.NotificationDTO;
import com.sn.onepay.notification.PaymentNotificationEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    /*Après le commit d'un paiement ou d'une annulation : notifie le client et le caissier (historique + push)*/
    void onPaymentEvent(PaymentNotificationEvent event);

    Page<NotificationDTO> getMyNotifications(String username, Boolean read, Pageable pageable);

    long countUnread(String username);

    NotificationDTO markAsRead(Long id, String username);

    int markAllAsRead(String username);

    void registerDeviceToken(String username, DeviceTokenDTO deviceToken);

    void unregisterDeviceToken(String username, String token);
}
