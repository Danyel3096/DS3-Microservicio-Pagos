package com.ds3.team8.payments_service.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message){
        super(message);
    }

    public BadRequestException(String message, Throwable cause){
        super(message, cause);
    }

    public BadRequestException(Throwable cause){
        super(cause);
    }
}
