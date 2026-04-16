package com.example.fitnationcommon.exception;

public class QrSessionAlreadyUsedException extends RuntimeException {
    public QrSessionAlreadyUsedException(String message) { super(message); }
}
