package com.example.cyclexbe.dto;

import java.util.List;

/**
 * Response DTO for shipper assigned deliveries list (S-60 F2)
 * GET /api/shipper/deliveries/assigned
 * Returns paginated list of assigned deliveries for current shipper
 */
public class ShipperAssignedDeliveryListResponse {

    private List<ShipperAssignedDeliveryItemDto> items;
    private Integer page;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;

    public ShipperAssignedDeliveryListResponse() {}

    public ShipperAssignedDeliveryListResponse(
            List<ShipperAssignedDeliveryItemDto> items,
            Integer page,
            Integer pageSize,
            Long totalElements,
            Integer totalPages) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<ShipperAssignedDeliveryItemDto> getItems() { return items; }
    public void setItems(List<ShipperAssignedDeliveryItemDto> items) { this.items = items; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }

    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}

