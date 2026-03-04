package com.example.fitnationrestapi.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) { super(message); }
}