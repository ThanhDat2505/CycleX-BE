package com.example.cyclexbe.dto;

import java.util.List;

/**
 * DTO for paginated pending transactions list response (S-52)
 */
public class SellerPendingTransactionsResponse {

    private List<PendingTransactionListItemResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private String sortBy;
    private String sortDir;
    private AppliedFilters appliedFilters;

    // No-args constructor
    public SellerPendingTransactionsResponse() {
    }

    // All-args constructor
    public SellerPendingTransactionsResponse(
            List<PendingTransactionListItemResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            String sortBy,
            String sortDir,
            AppliedFilters appliedFilters) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
        this.appliedFilters = appliedFilters;
    }

    // Getters
    public List<PendingTransactionListItemResponse> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public AppliedFilters getAppliedFilters() {
        return appliedFilters;
    }

    // Setters
    public void setContent(List<PendingTransactionListItemResponse> content) {
        this.content = content;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public void setAppliedFilters(AppliedFilters appliedFilters) {
        this.appliedFilters = appliedFilters;
    }

    /**
     * Nested class for applied filters info
     */
    public static class AppliedFilters {

        private String status;
        private String transactionType; // nullable

        public AppliedFilters() {
        }

        public AppliedFilters(String status, String transactionType) {
            this.status = status;
            this.transactionType = transactionType;
        }

        public String getStatus() {
            return status;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }
    }
}