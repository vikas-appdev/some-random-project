package com.gradlic.fts.erp.exception;

public class ApiException extends RuntimeException{
    public ApiException(String message){
        super(message);
    }
}
