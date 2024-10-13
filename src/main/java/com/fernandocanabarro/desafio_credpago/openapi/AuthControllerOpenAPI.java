package com.fernandocanabarro.desafio_credpago.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fernandocanabarro.desafio_credpago.dtos.AccountActivationResponse;
import com.fernandocanabarro.desafio_credpago.dtos.LoginRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.LoginResponseDTO;
import com.fernandocanabarro.desafio_credpago.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.UserDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface AuthControllerOpenAPI {

    @Operation(
        description = "Realizar Cadastro",
        summary = "Endpoint responsável por receber a requisição de Realizar Cadastro",
        responses = {
            @ApiResponse(description = "Cadastro realizado com sucesso",responseCode = "201"),
            @ApiResponse(description = "O e-mail já está em uso ou algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<UserDTO> register(@RequestBody @Valid RegistrationRequestDTO dto);

    @Operation(
        description = "Ativar Conta",
        summary = "Endpoint responsável por receber a requisição de Ativar Conta",
        responses = {
            @ApiResponse(description = "Conta Ativada com Sucesso",responseCode = "200"),
            @ApiResponse(description = "O Código de Ativação expirou",responseCode = "400"),
            @ApiResponse(description = "Código de Ativação não Encontrado",responseCode = "404")
        }
    )
    ResponseEntity<AccountActivationResponse> activate(@RequestParam(name = "code",defaultValue = "") String code);

    @Operation(
        description = "Efetuar Login",
        summary = "Endpoint responsável por receber a requisição de Efetuar Login",
        responses = {
            @ApiResponse(description = "Login Efetuado com Sucesso",responseCode = "200"),
            @ApiResponse(description = "A conta ainda não foi ativada",responseCode = "403"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto);
}
