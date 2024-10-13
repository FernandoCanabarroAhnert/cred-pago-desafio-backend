package com.fernandocanabarro.desafio_credpago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.User;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long>{

    Cart findByUser(User user);

}
