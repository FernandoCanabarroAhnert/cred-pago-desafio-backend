package com.fernandocanabarro.desafio_credpago.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fernandocanabarro.desafio_credpago.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String fullName;
    private String email;
    private Boolean activated;
    private List<RoleDTO> roles = new ArrayList<>();

    public UserDTO(User entity){
        id = entity.getId();
        fullName = entity.getFullName();
        email = entity.getEmail();
        activated = entity.getActivated();
        roles = entity.getRoles().stream().map(RoleDTO::new).toList();
    }
}
