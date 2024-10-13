package com.fernandocanabarro.desafio_credpago.services.exceptions;

public class ForbiddenException extends RuntimeException{

    public ForbiddenException(String msg){
        super(msg);
    }
}
