package com.uofc.acmeplex.logic.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AnnualBillingJob {

    private final PaymentService paymentService;

    //Runs 12midnight every day
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyJob() {
        paymentService.annualBilling();
    }
}