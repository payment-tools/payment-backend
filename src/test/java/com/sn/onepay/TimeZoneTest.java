package com.sn.onepay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

class TimeZoneTest {

    final TimeZone original = TimeZone.getDefault();

    @AfterEach
    void restore() {
        TimeZone.setDefault(original);
    }

    @Test
    void useUtcTimeZone_forcesUtcWhateverTheHostZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        OnePayBackendApplication.useUtcTimeZone();

        assertThat(TimeZone.getDefault().toZoneId().normalized()).isEqualTo(ZoneOffset.UTC);
    }
}
