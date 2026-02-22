package com.example.cyclexbe.dto;

/**
 * Response DTO cho submit inspection response
 */
public class SubmitInspectionResponseResult {

    private boolean submitted;
    private Integer responseId;
    private Integer listingId;
    private String newListingStatus; // WAITING_INSPECTOR_REVIEW

    public SubmitInspectionResponseResult() {
    }

    public SubmitInspectionResponseResult(boolean submitted, Integer responseId, Integer listingId, String newListingStatus) {
        this.submitted = submitted;
        this.responseId = responseId;
        this.listingId = listingId;
        this.newListingStatus = newListingStatus;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public String getNewListingStatus() {
        return newListingStatus;
    }

    public void setNewListingStatus(String newListingStatus) {
        this.newListingStatus = newListingStatus;
    }

    @Override
    public String toString() {
        return "SubmitInspectionResponseResult{" +
                "submitted=" + submitted +
                ", responseId=" + responseId +
                ", listingId=" + listingId +
                ", newListingStatus='" + newListingStatus + '\'' +
                '}';
    }
}