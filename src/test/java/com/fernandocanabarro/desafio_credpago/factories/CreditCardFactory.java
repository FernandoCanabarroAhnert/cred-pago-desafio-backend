package com.fernandocanabarro.desafio_credpago.factories;

import com.fernandocanabarro.desafio_credpago.entities.CreditCard;


import java.util.ArrayList;
import java.util.Arrays;

public class CreditCardFactory {

    public static CreditCard getCreditCard(){
        return new CreditCard(1L, "name", "1234123412341234", UserFactory.getUser(), 
            123, "12/24", new ArrayList<>(Arrays.asList()));
    }
}
