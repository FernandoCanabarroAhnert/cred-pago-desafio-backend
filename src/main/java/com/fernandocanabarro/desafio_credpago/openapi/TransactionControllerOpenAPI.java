package com.fernandocanabarro.desafio_credpago.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import com.fernandocanabarro.desafio_credpago.dtos.TransactionHistoryDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface TransactionControllerOpenAPI {

    @Operation(
        description = "Efetuar Transação",
        summary = "Endpoint responsável por receber a requisição de Efetuar Transação",
        responses = {
            @ApiResponse(description = "Transação Efetuada",responseCode = "201"),
            @ApiResponse(description = "Um usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "O Carrinho está Vazio ou o Cartão de Crédito não pertence ao usuário logado",responseCode = "403"),
            @ApiResponse(description = "Cartão de Crédito não encontrado",responseCode = "404"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<TransactionResponseDTO> buy(@RequestBody @Valid TransactionRequestDTO dto);
    
    @Operation(
        description = "Consultar as Transações do usuário logado",
        summary = "Endpoint responsável por receber a requisição de Consultar as Transações do usuário logado",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não autenticado faz a requisição",responseCode = "401")
        }
    )
    ResponseEntity<List<TransactionResponseDTO>> getMyTransactions();

    @Operation(
        description = "Consultar Transação por Id",
        summary = "Endpoint responsável por receber a requisição de Consultar Transação por Id",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Um usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "O usuário que fez a requisição não é dono da transação ou não é Administrador",responseCode = "403"),
            @ApiResponse(description = "Transação não encontrada",responseCode = "404")
        }
    )
    ResponseEntity<TransactionResponseDTO> findTransactionById(@PathVariable Long transactionId);

    @Operation(
        description = "Consultar o Histórico de Transações",
        summary = "Endpoint responsável por receber a requisição de Consultar o Histórico de Transações",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Usuário sem permissão faz a requisição",responseCode = "403"),
        }
    )
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsHistory();

    @Operation(
        description = "Consultar o Histórico de Transações de um Usuário",
        summary = "Endpoint responsável por receber a requisição de Consultar o Histórico de Transações de um Usuário",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Usuário sem permissão faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Usuário não encontrado",responseCode = "404")
        }
    )
    ResponseEntity<List<TransactionHistoryDTO>> getTransactionsHistoryByUserId(@PathVariable Long userId);

}
