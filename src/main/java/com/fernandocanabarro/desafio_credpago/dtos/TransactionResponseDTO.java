package com.fernandocanabarro.desafio_credpago.dtos;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.fernandocanabarro.desafio_credpago.entities.Transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO implements Serializable{

    private Long id;
    private String moment;
    private List<ProductCartItemDTO> products = new ArrayList<>();
    private Integer totalToPay;
    private CreditCardResponseDTO creditCard;
    private Long userId;
    private String userEmail;

    public TransactionResponseDTO(Transaction entity){
        id = entity.getId();
        moment = entity.getMoment().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        products = entity.getProducts().stream().map(x -> new ProductCartItemDTO(x)).toList();
        totalToPay = entity.getTotalToPay();
        creditCard = new CreditCardResponseDTO(entity.getCreditCard());
        userId = entity.getUser().getId();
        userEmail = entity.getUser().getEmail();
    }
}
