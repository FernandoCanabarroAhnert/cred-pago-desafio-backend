package com.fernandocanabarro.desafio_credpago.services.exceptions;

public class ExpiredActivationCodeException extends RuntimeException{

    public ExpiredActivationCodeException(String email){
        super("Código de Ativação Expirado! Um novo Código de Ativação será enviado para " + email);
    }
}
