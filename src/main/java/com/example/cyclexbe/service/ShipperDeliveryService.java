package com.example.cyclexbe.service;

import com.example.cyclexbe.domain.enums.PurchaseRequestStatus;
import com.example.cyclexbe.dto.ShipperAssignedDeliveryItemDto;
import com.example.cyclexbe.dto.ShipperAssignedDeliveryListResponse;
import com.example.cyclexbe.entity.Delivery;
import com.example.cyclexbe.repository.DeliveryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for S-60: Shipper Delivery Management
 * Handles retrieval of assigned deliveries for shippers
 */
@Service
@Transactional
public class ShipperDeliveryService {

    private final DeliveryRepository deliveryRepository;

    public ShipperDeliveryService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }


    /**
     * Get assigned deliveries for shipper with pagination
     * Returns only deliveries with status = ASSIGNED
     *
     * @param shipperId The shipper ID from authentication
     * @param pageable Pagination info (page, pageSize, sorting)
     * @return Paginated list of assigned deliveries with item details
     */

    @Transactional(readOnly = true)
    public ShipperAssignedDeliveryListResponse getAssignedDeliveries(Integer shipperId, Pageable pageable) {
        Page<Delivery> deliveriesPage =
                deliveryRepository.findByShipper_UserIdAndStatusAndTransaction_Status(
                        shipperId,
                        "ASSIGNED",
                        PurchaseRequestStatus.SELLER_CONFIRMED,
                        pageable
                );

        List<ShipperAssignedDeliveryItemDto> items = deliveriesPage.getContent()
                .stream()
                .map(this::mapToItemDto)
                .toList();

        return new ShipperAssignedDeliveryListResponse(
                items,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                deliveriesPage.getTotalElements(),
                deliveriesPage.getTotalPages()
        );
    }

    /**
     * Map Delivery entity to item DTO
     * Includes request ID, status, listing info, and seller info
     */
    private ShipperAssignedDeliveryItemDto mapToItemDto(Delivery delivery) {
        return new ShipperAssignedDeliveryItemDto(
                delivery.getTransaction().getRequestId(),
                delivery.getStatus(),
                delivery.getListing().getListingId(),
                delivery.getListing().getTitle(),
                delivery.getListing().getSeller().getUserId(),
                delivery.getListing().getSeller().getFullName(),
                delivery.getUpdatedAt()
        );
    }

}

