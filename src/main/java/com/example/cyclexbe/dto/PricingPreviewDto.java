package com.example.cyclexbe.dto;

import java.math.BigDecimal;

/**
 * Pricing preview info for purchase request initialization and review
 */
public class PricingPreviewDto {

    private Integer depositRate;  // e.g. 10 (percent)
    private BigDecimal suggestedDepositAmount;  // 10% of listing price
    private BigDecimal platformFee;  // TODO: Define fee rules
    private BigDecimal inspectionFee;  // TODO: Define fee rules

    public PricingPreviewDto() {}

    public PricingPreviewDto(
            Integer depositRate,
            BigDecimal suggestedDepositAmount,
            BigDecimal platformFee,
            BigDecimal inspectionFee) {
        this.depositRate = depositRate;
        this.suggestedDepositAmount = suggestedDepositAmount;
        this.platformFee = platformFee;
        this.inspectionFee = inspectionFee;
    }

    public Integer getDepositRate() {
        return depositRate;
    }

    public void setDepositRate(Integer depositRate) {
        this.depositRate = depositRate;
    }

    public BigDecimal getSuggestedDepositAmount() {
        return suggestedDepositAmount;
    }

    public void setSuggestedDepositAmount(BigDecimal suggestedDepositAmount) {
        this.suggestedDepositAmount = suggestedDepositAmount;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getInspectionFee() {
        return inspectionFee;
    }

    public void setInspectionFee(BigDecimal inspectionFee) {
        this.inspectionFee = inspectionFee;
    }
}

