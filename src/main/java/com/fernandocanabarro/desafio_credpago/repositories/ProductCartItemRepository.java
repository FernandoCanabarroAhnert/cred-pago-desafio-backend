package com.fernandocanabarro.desafio_credpago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.entities.pk.ProductCartItemPK;

@Repository
public interface ProductCartItemRepository extends JpaRepository<ProductCartItem,ProductCartItemPK>{

}
