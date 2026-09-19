package com.sn.onepay.scheduling;

import com.sn.onepay.repository.QRCodeClientRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional
public class QRCodeClientPurgeJob {

    final QRCodeClientRepository qrCodeClientRepository;

    @Scheduled(fixedRateString = "${onepay.qrcode.client.purge-interval-ms:300000}")
    public void purgeExpiredQrCodes() {
        int purged = qrCodeClientRepository.deactivateExpired(LocalDateTime.now());
        if (purged > 0)
            log.info("Purged {} expired client QR code(s)", purged);
    }
}
