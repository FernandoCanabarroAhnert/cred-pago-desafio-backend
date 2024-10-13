package com.fernandocanabarro.desafio_credpago.services.exceptions;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Long id){
        super("Recurso não encontrado! Id = " + id);
    }

    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
