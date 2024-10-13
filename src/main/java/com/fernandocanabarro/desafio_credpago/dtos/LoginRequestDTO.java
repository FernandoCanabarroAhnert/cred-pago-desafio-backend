package com.fernandocanabarro.desafio_credpago.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "Campo Requerido")
    private String email;
    @NotBlank(message = "Campo Requerido")
    private String password;
}
