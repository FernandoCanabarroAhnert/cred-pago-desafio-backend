package com.fernandocanabarro.desafio_credpago.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import com.fernandocanabarro.desafio_credpago.dtos.CreditCardRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.CreditCardResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface CreditCardControllerOpenAPI {

    @Operation(
        description = "Adicionar um Cartão de Crédito",
        summary = "Endpoint responsável por receber a requisição de Adicionar um Cartão de Crédito",
        responses = {
            @ApiResponse(description = "Cartão Criado",responseCode = "201"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Algum dado da requisição está inválido ou o Cartão já está em uso",responseCode = "422")
        }
    )
    ResponseEntity<CreditCardResponseDTO> addCreditCard(@RequestBody @Valid CreditCardRequestDTO dto);
    
    @Operation(
        description = "Consultar os Cartões de Crédito do usuário logado",
        summary = "Endpoint responsável por receber a requisição de Consultar os Cartões de Crédito do usuário logado",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
        }
    )
    ResponseEntity<List<CreditCardResponseDTO>> findMyCards();

    @Operation(
        description = "Consultar Cartões de Crédito por Usuário",
        summary = "Endpoint responsável por receber a requisição de Consultar Cartões de Crédito por Usuário",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Usuário sem permissão faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Usuário não encontrado",responseCode = "404")
        }
    )
    ResponseEntity<List<CreditCardResponseDTO>> findCreditCardsByUserId(@PathVariable Long userId);


}
