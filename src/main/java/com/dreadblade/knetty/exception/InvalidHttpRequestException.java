package com.dreadblade.knetty.exception;

public class InvalidHttpRequestException extends Exception {
    public InvalidHttpRequestException() {

    }
    public InvalidHttpRequestException(String message) {
        super(message);
    }
}
