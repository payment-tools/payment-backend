package com.sn.onepay.scheduling;

import com.sn.onepay.services.BillsService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MonthlyBillsJobTest {

    @Test
    void generatesTheBillsOfThePreviousMonth() {
        BillsService billsService = mock(BillsService.class);
        Clock firstOfJanuary = Clock.fixed(Instant.parse("2027-01-01T00:05:00Z"), ZoneOffset.UTC);

        new MonthlyBillsJob(billsService, firstOfJanuary).generatePreviousMonthBills();

        verify(billsService).generateMonthlyBills(YearMonth.of(2026, 12));
    }
}
