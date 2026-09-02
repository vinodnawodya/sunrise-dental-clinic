package com.sunrisedental.pattern.strategy;

import com.sunrisedental.entity.Treatment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Standard treatments are billed at exactly the treatment's base cost. */
@Component
public class StandardTreatmentPricing implements BillingStrategy {

    @Override
    public BigDecimal calculateCost(Treatment treatment) {
        return treatment.getBaseCost();
    }
}
