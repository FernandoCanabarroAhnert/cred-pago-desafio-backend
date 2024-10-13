package com.fernandocanabarro.desafio_credpago.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.Transaction;
import com.fernandocanabarro.desafio_credpago.projections.TransactionHistoryProjection;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>{

    @Query(nativeQuery = true,value = 
        """
        SELECT t.id,u.id AS userId,c.card_number, t.total_to_pay, t.moment
        FROM transactions AS t
        INNER JOIN users AS u ON t.user_id = u.id
        INNER JOIN credit_cards AS c ON c.user_id = u.id
        WHERE u.id = :id
        """
    )
    List<TransactionHistoryProjection> getTransactionHistoryByUserId(Long id);

    @Query(nativeQuery = true,value = 
        """
        SELECT t.id,u.id AS userId,c.card_number, t.total_to_pay, t.moment
        FROM transactions AS t
        INNER JOIN users AS u ON t.user_id = u.id
        INNER JOIN credit_cards AS c ON c.user_id = u.id
        """
    )
    List<TransactionHistoryProjection> getTransactionHistory();
}
