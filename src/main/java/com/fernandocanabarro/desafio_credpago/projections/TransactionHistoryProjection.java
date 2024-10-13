package com.fernandocanabarro.desafio_credpago.projections;

import java.time.LocalDateTime;

public interface TransactionHistoryProjection {

    Long getId();
    Long getUserId();
    String getCardNumber();
    Integer getTotalToPay();
    LocalDateTime getMoment();
}
