package com.example.cyclexbe.domain.enums;

public enum BikeListingStatus {
    DRAFT,
    APPROVED,
    REJECTED,
    PENDING,
    DELETED,
    ARCHIVED, // Status for listing that's archived (locked chat thread)
    REVIEWING,
    NEED_MORE_INFO,
    WAITING_INSPECTOR_REVIEW,
    SOLD // Delivery confirmed – bike has been sold
}
