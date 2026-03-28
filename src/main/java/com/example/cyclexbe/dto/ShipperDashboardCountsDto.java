package com.example.cyclexbe.dto;

/**
 * DTO for shipper dashboard counts
 * Contains count of deliveries in each status bucket
 */
public class ShipperDashboardCountsDto {

    private Integer assigned;
    private Integer inProgress;
    private Integer delivered;
    private Integer failed;

    public ShipperDashboardCountsDto() {
    }

    public ShipperDashboardCountsDto(Integer assigned, Integer inProgress, Integer delivered, Integer failed) {
        this.assigned = assigned;
        this.inProgress = inProgress;
        this.delivered = delivered;
        this.failed = failed;
    }

    public Integer getAssigned() {
        return assigned;
    }

    public void setAssigned(Integer assigned) {
        this.assigned = assigned;
    }

    public Integer getInProgress() {
        return inProgress;
    }

    public void setInProgress(Integer inProgress) {
        this.inProgress = inProgress;
    }

    public Integer getDelivered() {
        return delivered;
    }

    public void setDelivered(Integer delivered) {
        this.delivered = delivered;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }
}
