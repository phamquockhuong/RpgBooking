package com.example.RpgBooking.exception;

public class InvalidPlayerCountException extends RuntimeException {
    public InvalidPlayerCountException(String message) {
        super(message);
    }
}
