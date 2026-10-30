package com.sn.onepay.services.impl;

import com.sn.onepay.dto.DeviceTokenDTO;
import com.sn.onepay.dto.NotificationDTO;
import com.sn.onepay.entity.Cashier;
import com.sn.onepay.entity.Client;
import com.sn.onepay.entity.DeviceToken;
import com.sn.onepay.entity.Notification;
import com.sn.onepay.entity.Payment;
import com.sn.onepay.enumeration.NotificationType;
import com.sn.onepay.exceptions.ResourceNotFoundException;
import com.sn.onepay.mapper.NotificationMapper;
import com.sn.onepay.notification.PaymentNotificationEvent;
import com.sn.onepay.notification.PushMessage;
import com.sn.onepay.notification.PushSender;
import com.sn.onepay.repository.DeviceTokenRepository;
import com.sn.onepay.repository.NotificationRepository;
import com.sn.onepay.repository.PaymentRepository;
import com.sn.onepay.services.NotificationService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationServiceImpl implements NotificationService {

    final NotificationRepository notificationRepository;
    final DeviceTokenRepository deviceTokenRepository;
    final PaymentRepository paymentRepository;
    final NotificationMapper notificationMapper;
    final PushSender pushSender;

    /*Après le commit : un échec de notification ne doit jamais annuler le paiement*/
    @Override
    @TransactionalEventListener
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void onPaymentEvent(PaymentNotificationEvent event) {
        try {
            Payment payment = paymentRepository.findById(event.paymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment", "ID", event.paymentId()));
            Client client = payment.getClient();
            Cashier cashier = payment.getCashier();
            String amount = formatAmount(payment.getAmount());
            String sales = cashier.getSales().getName();
            String payer = client.getFirstname() + " " + client.getLastname() + " (" + client.getEnterprise().getName() + ")";

            if (event.type() == NotificationType.PAYMENT_CREATED) {
                notify(client.getUsername(), event, "Paiement effectué", "Paiement de " + amount + " chez " + sales);
                notify(cashier.getUsername(), event, "Paiement reçu", "Paiement reçu : " + amount + " de " + payer);
            } else {
                notify(client.getUsername(), event, "Paiement annulé", "Paiement de " + amount + " chez " + sales + " annulé");
                notify(cashier.getUsername(), event, "Paiement annulé", "Paiement de " + amount + " de " + payer + " annulé");
            }
        } catch (RuntimeException exception) {
            log.error("Failed to notify payment {} ({}): {}", event.paymentId(), event.type(), exception.getMessage());
        }
    }

    private void notify(String username, PaymentNotificationEvent event, String title, String message) {
        Notification notification = new Notification();
        notification.setRecipientUsername(username);
        notification.setType(event.type());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPaymentId(event.paymentId());
        notification.setRead(false);
        Notification saved = notificationRepository.save(notification);

        List<String> tokens = deviceTokenRepository.findByUsername(username).stream().map(DeviceToken::getToken).toList();
        if (tokens.isEmpty()) {
            return;
        }
        PushMessage push = new PushMessage(title, message, Map.of(
                "type", event.type().name(),
                "paymentId", String.valueOf(event.paymentId()),
                "notificationId", String.valueOf(saved.getId())));
        // Un échec d'envoi push ne doit pas empêcher l'enregistrement de la notification de l'autre destinataire
        try {
            List<String> staleTokens = pushSender.send(tokens, push);
            if (!staleTokens.isEmpty()) {
                deviceTokenRepository.deleteByTokenIn(staleTokens);
                log.info("Removed {} stale device token(s) of user {}", staleTokens.size(), username);
            }
        } catch (RuntimeException exception) {
            log.error("Failed to push notification {} to user {}: {}", saved.getId(), username, exception.getMessage());
        }
    }

    /*2500.0 → « 2 500 FCFA » (espace insécable, comme l'app mobile)*/
    static String formatAmount(Double amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        return new DecimalFormat("#,##0", symbols).format(amount) + " FCFA";
    }

    @Override
    public Page<NotificationDTO> getMyNotifications(String username, Boolean read, Pageable pageable) {
        Page<Notification> page = read == null
                ? notificationRepository.findByRecipientUsername(username, pageable)
                : notificationRepository.findByRecipientUsernameAndRead(username, read, pageable);
        return page.map(notificationMapper::asDTO);
    }

    @Override
    public long countUnread(String username) {
        return notificationRepository.countByRecipientUsernameAndReadFalse(username);
    }

    @Override
    @Transactional
    public NotificationDTO markAsRead(Long id, String username) {
        // Une notification d'un autre utilisateur est traitée comme inexistante
        Notification notification = notificationRepository.findByIdAndRecipientUsername(id, username)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "ID", id));
        notification.setRead(true);
        return notificationMapper.asDTO(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public int markAllAsRead(String username) {
        return notificationRepository.markAllAsRead(username);
    }

    @Override
    @Transactional
    public void registerDeviceToken(String username, DeviceTokenDTO deviceToken) {
        // Un jeton appartient à un seul appareil : s'il existe déjà, il est rattaché à l'utilisateur connecté
        DeviceToken token = deviceTokenRepository.findByToken(deviceToken.token()).orElseGet(DeviceToken::new);
        token.setToken(deviceToken.token());
        token.setUsername(username);
        token.setPlatform(deviceToken.platform());
        deviceTokenRepository.save(token);
        log.info("Registered {} device token for user {}", deviceToken.platform(), username);
    }

    @Override
    @Transactional
    public void unregisterDeviceToken(String username, String token) {
        deviceTokenRepository.deleteByTokenAndUsername(token, username);
    }
}
