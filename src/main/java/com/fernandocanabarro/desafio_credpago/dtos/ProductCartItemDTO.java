package com.fernandocanabarro.desafio_credpago.dtos;

import java.io.Serializable;

import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCartItemDTO implements Serializable{

    @NotNull(message = "Campo Requerido")
    private Long productId;
    private String artist;
    private String year;
    private String album;
    private Integer price;
    private String thumb;
    @NotNull(message = "Campo Requerido")
    private Integer quantity;

    public ProductCartItemDTO(ProductCartItem entity){
        productId = entity.getProduct().getId();
        artist = entity.getProduct().getArtist();
        year = entity.getProduct().getYear();
        album = entity.getProduct().getAlbum();
        price = entity.getPrice();
        thumb = entity.getProduct().getThumb();
        quantity = entity.getQuantity();
    }

}
