package com.example.cyclexbe.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO for confirming a transaction (S-52 optional feature)
 */
public class ConfirmTransactionRequest {

    @Size(max = 500, message = "Note must not exceed 500 characters")
    private String note;

    // No-args constructor
    public ConfirmTransactionRequest() {
    }

    // All-args constructor
    public ConfirmTransactionRequest(String note) {
        this.note = note;
    }

    // Getters
    public String getNote() {
        return note;
    }

    // Setters
    public void setNote(String note) {
        this.note = note;
    }
}

