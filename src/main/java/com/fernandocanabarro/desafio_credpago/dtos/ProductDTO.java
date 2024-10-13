package com.fernandocanabarro.desafio_credpago.dtos;

import com.fernandocanabarro.desafio_credpago.entities.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable{

    private Long productId;
    @NotBlank(message = "Campo Requerido")
    private String artist;
    @NotBlank(message = "Campo Requerido")
    private String year;
    @NotBlank(message = "Campo Requerido")
    private String album;
    @NotNull(message = "Campo Requerido")
    @Positive(message = "Valor deve ser positivo")
    private Integer price;
    @NotBlank(message = "Campo Requerido")
    private String thumb;

    public ProductDTO(Product entity){
        productId = entity.getId();
        artist = entity.getArtist();
        year = entity.getYear();
        album = entity.getAlbum();
        price = entity.getPrice();
        thumb = entity.getThumb();
    }
}
