package com.vishal.electronicsstore.exception;

public class BadAPIRequestException extends RuntimeException {
    
    public BadAPIRequestException() {
        super("Bad request!");
    }

    public BadAPIRequestException(String message) {
        super(message);
    }

}
