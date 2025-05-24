package com.vishal.electronicsstore.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException() {
        super("User already exists! Please try with another email.");
    }

    public UserAlreadyExistsException(String message) {
        super(message);
    }

}
