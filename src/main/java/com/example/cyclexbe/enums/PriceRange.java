package com.example.cyclexbe.enums;

import java.math.BigDecimal;

public enum PriceRange {

    UNDER_5M(null, new BigDecimal("5000000")),
    FROM_5M_TO_10M(new BigDecimal("5000000"), new BigDecimal("10000000")),
    ABOVE_10M(new BigDecimal("10000000"), null);

    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    PriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }
}

