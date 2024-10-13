package com.fernandocanabarro.desafio_credpago.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.desafio_credpago.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{

    Role findByAuthority(String authority);
}
