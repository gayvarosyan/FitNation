package com.example.fitnationcommon.exception;

public class QrSessionExpiredException extends RuntimeException {
    public QrSessionExpiredException(String message) { super(message); }
}
