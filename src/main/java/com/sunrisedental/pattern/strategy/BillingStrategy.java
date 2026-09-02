package com.sunrisedental.pattern.strategy;

import com.sunrisedental.entity.Treatment;

import java.math.BigDecimal;

/**
 * Strategy pattern: pluggable pricing algorithms for a treatment's total
 * cost. The concrete strategy is picked by {@link Treatment#getCategory()}.
 */
public interface BillingStrategy {

    BigDecimal calculateCost(Treatment treatment);
}
