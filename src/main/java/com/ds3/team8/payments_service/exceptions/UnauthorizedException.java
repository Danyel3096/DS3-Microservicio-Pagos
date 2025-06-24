package com.ds3.team8.payments_service.exceptions;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause){
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause){
        super(cause);
    }
}
