package com.sn.onepay.notification;

import java.util.List;

public interface PushSender {

    /*Envoie le message à chaque appareil ; renvoie les jetons rejetés définitivement (appareil désinscrit,
      jeton invalide), à supprimer*/
    List<String> send(List<String> tokens, PushMessage message);
}
