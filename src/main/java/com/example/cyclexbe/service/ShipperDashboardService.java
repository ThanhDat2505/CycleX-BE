package com.example.cyclexbe.service;

import com.example.cyclexbe.dto.ShipperDashboardCountsDto;
import com.example.cyclexbe.dto.ShipperDashboardSummaryResponse;
import com.example.cyclexbe.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for S-60: Shipper Dashboard
 * Handles dashboard summary and delivery management for shippers
 */
@Service
@Transactional
public class ShipperDashboardService {

    private final DeliveryRepository deliveryRepository;

    public ShipperDashboardService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Get dashboard summary for shipper
     * Computes counts for the 3 buckets:
     * - assigned: status = ASSIGNED
     * - inProgress: status = IN_PROGRESS
     * - failed: status = FAILED or CANCELLED
     *
     * @param shipperId The shipper ID from authentication
     * @return Dashboard summary with counts and timestamp
     */
    @Transactional(readOnly = true)
    public ShipperDashboardSummaryResponse getSummary(Integer shipperId) {
        long assignedCount = deliveryRepository.countByShipperAndStatus(shipperId, "ASSIGNED");
        long inProgressCount = deliveryRepository.countByShipperAndStatus(shipperId, "IN_PROGRESS");

        // Failed = FAILED or CANCELLED
        long failedCount = deliveryRepository.countByShipperAndStatus(shipperId, "FAILED") +
                           deliveryRepository.countByShipperAndStatus(shipperId, "CANCELLED");

        ShipperDashboardCountsDto counts = new ShipperDashboardCountsDto(
                (int) assignedCount,
                (int) inProgressCount,
                (int) failedCount
        );

        ShipperDashboardSummaryResponse response = new ShipperDashboardSummaryResponse(
                counts,
                LocalDateTime.now()
        );

        return response;
    }
}

