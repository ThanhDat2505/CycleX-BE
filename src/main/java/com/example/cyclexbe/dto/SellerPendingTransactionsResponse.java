package com.example.cyclexbe.dto;

}
    }
        }
            this.transactionType = transactionType;
        public void setTransactionType(String transactionType) {

        }
            return transactionType;
        public String getTransactionType() {

        }
            this.status = status;
        public void setStatus(String status) {

        }
            return status;
        public String getStatus() {

        }
            this.transactionType = transactionType;
            this.status = status;
        public AppliedFilters(String status, String transactionType) {

        }
        public AppliedFilters() {

        private String transactionType; // nullable
        private String status;
    public static class AppliedFilters {
     */
     * Nested class for applied filters info
    /**

    }
        this.appliedFilters = appliedFilters;
    public void setAppliedFilters(AppliedFilters appliedFilters) {

    }
        this.sortDir = sortDir;
    public void setSortDir(String sortDir) {

    }
        this.sortBy = sortBy;
    public void setSortBy(String sortBy) {

    }
        this.totalPages = totalPages;
    public void setTotalPages(int totalPages) {

    }
        this.totalElements = totalElements;
    public void setTotalElements(long totalElements) {

    }
        this.size = size;
    public void setSize(int size) {

    }
        this.page = page;
    public void setPage(int page) {

    }
        this.content = content;
    public void setContent(List<PendingTransactionListItemResponse> content) {
    // Setters

    }
        return appliedFilters;
    public AppliedFilters getAppliedFilters() {

    }
        return sortDir;
    public String getSortDir() {

    }
        return sortBy;
    public String getSortBy() {

    }
        return totalPages;
    public int getTotalPages() {

    }
        return totalElements;
    public long getTotalElements() {

    }
        return size;
    public int getSize() {

    }
        return page;
    public int getPage() {

    }
        return content;
    public List<PendingTransactionListItemResponse> getContent() {
    // Getters

    }
        this.appliedFilters = appliedFilters;
        this.sortDir = sortDir;
        this.sortBy = sortBy;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
        this.page = page;
        this.content = content;
            AppliedFilters appliedFilters) {
            String sortDir,
            String sortBy,
            int totalPages,
            long totalElements,
            int size,
            int page,
            List<PendingTransactionListItemResponse> content,
    public SellerPendingTransactionsResponse(
    // All-args constructor

    }
    public SellerPendingTransactionsResponse() {
    // No-args constructor

    private AppliedFilters appliedFilters;
    private String sortDir;
    private String sortBy;
    private int totalPages;
    private long totalElements;
    private int size;
    private int page;
    private List<PendingTransactionListItemResponse> content;

public class SellerPendingTransactionsResponse {
 */
 * DTO for paginated pending transactions list response (S-52)
/**

import java.util.List;

