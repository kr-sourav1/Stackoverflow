package com.stackoverflow.beta.exception;

import java.util.Date;

public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String errorCode;
    private String details;

    public ErrorDetails(Date timestamp, String message, String errorCode, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.errorCode = errorCode;
        this.details = details;
    }

    // Getters for all fields
    public java.util.Date getTimestamp() { return timestamp; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public String getDetails() { return details; }
}