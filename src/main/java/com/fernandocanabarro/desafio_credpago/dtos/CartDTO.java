package com.fernandocanabarro.desafio_credpago.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fernandocanabarro.desafio_credpago.entities.Cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO {

    private Long cartId;
    private Long clientId;
    private String clientName;
    private List<ProductCartItemDTO> products = new ArrayList<>();

    public CartDTO(Cart entity){
        cartId = entity.getId();
        clientId = entity.getUser().getId();
        clientName = entity.getUser().getFullName();
        products = entity.getProducts().stream().map(ProductCartItemDTO::new).toList();
    }

}
