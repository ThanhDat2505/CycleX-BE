package com.example.cyclexbe.exception;

public class ErrorRespone {
    private String message;
    private int status;
    private Long timestam;

    public ErrorRespone(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestam = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getTimestam() {
        return timestam;
    }

    public void setTimestam(Long timestam) {
        this.timestam = timestam;
    }
}
