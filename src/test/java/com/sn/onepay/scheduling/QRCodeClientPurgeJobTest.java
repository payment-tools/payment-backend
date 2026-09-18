package com.sn.onepay.scheduling;

import com.sn.onepay.repository.QRCodeClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QRCodeClientPurgeJobTest {

    @Mock
    QRCodeClientRepository qrCodeClientRepository;

    @InjectMocks
    QRCodeClientPurgeJob purgeJob;

    @Test
    void purgeExpiredQrCodes_deactivatesExpiredCodes() {
        when(qrCodeClientRepository.deactivateExpired(any(LocalDateTime.class))).thenReturn(3);

        purgeJob.purgeExpiredQrCodes();

        verify(qrCodeClientRepository).deactivateExpired(any(LocalDateTime.class));
    }

    @Test
    void purgeExpiredQrCodes_noopWhenNothingExpired() {
        when(qrCodeClientRepository.deactivateExpired(any(LocalDateTime.class))).thenReturn(0);

        purgeJob.purgeExpiredQrCodes();

        verify(qrCodeClientRepository).deactivateExpired(any(LocalDateTime.class));
    }
}
