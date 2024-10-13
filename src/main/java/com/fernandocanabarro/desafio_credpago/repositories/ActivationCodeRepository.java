package com.fernandocanabarro.desafio_credpago.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.ActivationCode;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode,Long>{

    Optional<ActivationCode> findByCode(String code);
}
