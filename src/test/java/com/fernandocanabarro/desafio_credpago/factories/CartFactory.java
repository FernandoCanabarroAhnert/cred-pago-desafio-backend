package com.fernandocanabarro.desafio_credpago.factories;

import com.fernandocanabarro.desafio_credpago.entities.Cart;

import java.util.Arrays;
import java.util.ArrayList;

public class CartFactory {

    public static Cart getCart(){
        return new Cart(1L, UserFactory.getUser(), new ArrayList<>(Arrays.asList()));
    }
}
