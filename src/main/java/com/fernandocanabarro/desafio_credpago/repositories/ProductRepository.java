package com.fernandocanabarro.desafio_credpago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>{

}
