package com.sn.onepay.notification;

import com.sn.onepay.enumeration.NotificationType;

/*Publié par PaymentServiceImpl à la création et à l'annulation d'un paiement, traité après le commit.
  Point d'extension : remplaçable par un message Kafka si les notifications passent dans un service séparé.*/
public record PaymentNotificationEvent(Long paymentId, NotificationType type) {
}
