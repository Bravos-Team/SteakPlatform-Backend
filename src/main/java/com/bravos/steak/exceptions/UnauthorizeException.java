package com.bravos.steak.exceptions;

public class UnauthorizeException extends RuntimeException {
    public UnauthorizeException(String message) {
        super(message);
    }
}
