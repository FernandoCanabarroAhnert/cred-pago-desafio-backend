package com.fernandocanabarro.desafio_credpago.factories;

import com.fernandocanabarro.desafio_credpago.entities.Transaction;

import java.util.ArrayList;
import java.util.Arrays;

import java.time.LocalDateTime;

public class TransactionFactory {

    public static Transaction geTransaction(){
        Transaction transaction = new  Transaction(1L, 10, new ArrayList<>(Arrays.asList()), 
            UserFactory.getUser(), CreditCardFactory.getCreditCard(), LocalDateTime.now());
        transaction.getProducts().add(ProductFactory.getProductCartItem());
        return transaction;
    }
}
