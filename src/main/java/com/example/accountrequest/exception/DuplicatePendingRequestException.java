package com.example.accountrequest.exception;

public class DuplicatePendingRequestException extends RuntimeException {

    public DuplicatePendingRequestException(String message) {
        super(message);
    }
}