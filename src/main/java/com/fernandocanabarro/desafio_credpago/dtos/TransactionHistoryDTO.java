package com.fernandocanabarro.desafio_credpago.dtos;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

import com.fernandocanabarro.desafio_credpago.projections.TransactionHistoryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryDTO implements Serializable{

    private Long transactionId;
    private Long clientId;
    private String cardNumber;
    private Integer value;
    private String date;

    public TransactionHistoryDTO(TransactionHistoryProjection projection){
        transactionId = projection.getId();
        clientId = projection.getUserId();
        cardNumber = "**** **** **** " + projection.getCardNumber().substring(12);
        value = projection.getTotalToPay();
        date = projection.getMoment().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }
}
