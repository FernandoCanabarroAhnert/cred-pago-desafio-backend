package com.fernandocanabarro.desafio_credpago.factories;

import com.fernandocanabarro.desafio_credpago.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class UserFactory {

    public static User getUser(){
        User user = new User(1L, "name", "email@gmail.com", "password",
             false, new HashSet<>(Arrays.asList()), new ArrayList<>(Arrays.asList()), new ArrayList<>(Arrays.asList()), new ArrayList<>(Arrays.asList()));
        user.addRole(RoleFactory.getUserRole());
        return user;
    }

}
