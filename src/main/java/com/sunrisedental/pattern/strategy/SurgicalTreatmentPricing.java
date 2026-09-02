package com.sunrisedental.pattern.strategy;

import com.sunrisedental.entity.Treatment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Surgical treatments carry a 15% surcharge on top of the base cost, to
 * cover the additional theatre time and materials they require.
 */
@Component
public class SurgicalTreatmentPricing implements BillingStrategy {

    private static final BigDecimal SURGICAL_SURCHARGE_MULTIPLIER = new BigDecimal("1.15");

    @Override
    public BigDecimal calculateCost(Treatment treatment) {
        return treatment.getBaseCost()
                .multiply(SURGICAL_SURCHARGE_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
