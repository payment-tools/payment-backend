package com.sn.onepay.scheduling;

import com.sn.onepay.services.BillsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.YearMonth;

/*Le 1er de chaque mois à 00:05 UTC : factures du mois écoulé, une par partenariat actif ayant des paiements.
  La génération est idempotente : une relance ne crée jamais de doublon.*/
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MonthlyBillsJob {

    final BillsService billsService;
    final Clock clock;

    @Scheduled(cron = "${onepay.bills.cron:0 5 0 1 * *}", zone = "UTC")
    public void generatePreviousMonthBills() {
        billsService.generateMonthlyBills(YearMonth.now(clock).minusMonths(1));
    }
}
