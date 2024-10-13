package com.fernandocanabarro.desafio_credpago.factories;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.desafio_credpago.entities.Product;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;

public class ProductFactory {

    public static Product getProduct(){
        return new Product(1L, "artist", "2024", "album", 10, "thumb", new ArrayList<>(Arrays.asList()));
    }

    public static ProductCartItem getProductCartItem(){
        return new ProductCartItem(CartFactory.getCart(), ProductFactory.getProduct(), 1, 10);
    }
}
