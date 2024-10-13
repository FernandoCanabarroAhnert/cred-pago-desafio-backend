package com.fernandocanabarro.desafio_credpago.dtos;

import java.io.Serializable;

import com.fernandocanabarro.desafio_credpago.entities.CreditCard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardResponseDTO implements Serializable{

    private Long id;
    private String holderName;
    private String cardNumber;
    private String expirationDate;

    public CreditCardResponseDTO(CreditCard entity){
        id = entity.getId();
        holderName = entity.getHolderName();
        cardNumber = "**** **** **** " + entity.getCardNumber().substring(12);
        expirationDate = entity.getExpirationDate();
    }
}
