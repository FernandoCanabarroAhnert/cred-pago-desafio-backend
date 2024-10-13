package com.fernandocanabarro.desafio_credpago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.CreditCard;
import com.fernandocanabarro.desafio_credpago.entities.User;

import java.util.Optional;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard,Long>{

    Optional<CreditCard> findByCardNumber(String number);

    List<CreditCard> findByUser(User user);
}
