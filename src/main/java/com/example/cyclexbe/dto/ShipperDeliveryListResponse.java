package com.example.cyclexbe.dto;

import java.util.List;

/**
 * Paginated response for shipper deliveries list (S-61 F1/F2)
 * GET /api/shipper/deliveries?status=...&page=...&size=...
 */
public class ShipperDeliveryListResponse {

    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private List<ShipperDeliveryListItemDto> items;

    public ShipperDeliveryListResponse() {}

    public ShipperDeliveryListResponse(
            Integer page,
            Integer size,
            Long totalElements,
            Integer totalPages,
            List<ShipperDeliveryListItemDto> items) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.items = items;
    }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }

    public List<ShipperDeliveryListItemDto> getItems() { return items; }
    public void setItems(List<ShipperDeliveryListItemDto> items) { this.items = items; }
}

