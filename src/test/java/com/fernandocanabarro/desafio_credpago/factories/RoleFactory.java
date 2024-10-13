package com.fernandocanabarro.desafio_credpago.factories;

import com.fernandocanabarro.desafio_credpago.entities.Role;

public class RoleFactory {

    public static Role getUserRole(){
        return new Role(1L,"ROLE_USER");
    }

    public static Role getAdminRole(){
        return new Role(2L, "ROLE_ADMIN");
    }
}
