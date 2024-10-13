package com.fernandocanabarro.desafio_credpago.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardRequestDTO {

    @NotBlank(message = "Campo Requerido")
    private String holderName;
    @NotBlank(message = "Campo Requerido")
    private String cardNumber;
    @NotBlank(message = "Campo Requerido")
    @Size(min = 3,max = 3,message = "CVV deve ter 3 caracteres")
    private String cvv;
    @NotBlank(message = "Data deve estar em formato válido: mm/yy")
    private String expirationDate;
}
