package com.sunrisedental.pattern.factory;

import com.sunrisedental.entity.Appointment;
import com.sunrisedental.entity.Bill;
import com.sunrisedental.pattern.strategy.BillingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Factory pattern: builds a fully-populated {@link Bill} for an appointment,
 * delegating the cost calculation to whichever {@link BillingStrategy} the
 * caller selected (see {@link com.sunrisedental.service.BillingService}).
 */
public final class BillFactory {

    private BillFactory() {
    }

    public static Bill createBill(Appointment appointment, BillingStrategy billingStrategy) {
        BigDecimal totalCost = billingStrategy.calculateCost(appointment.getTreatment());
        return Bill.builder()
                .appointment(appointment)
                .totalCost(totalCost)
                .generatedDate(LocalDateTime.now())
                .build();
    }
}
