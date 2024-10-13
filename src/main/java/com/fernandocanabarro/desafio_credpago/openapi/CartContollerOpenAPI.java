package com.fernandocanabarro.desafio_credpago.openapi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.fernandocanabarro.desafio_credpago.dtos.CartDTO;
import com.fernandocanabarro.desafio_credpago.dtos.ProductCartItemDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface CartContollerOpenAPI {

    @Operation(
        description = "Adicionar um Produto ao Carrinho",
        summary = "Endpoint responsável por receber a requisição de Adicionar um Produto ao Carrinho",
        responses = {
            @ApiResponse(description = "Produto adicionado ao Carrinho",responseCode = "201"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<CartDTO> addProductToCart(@RequestBody @Valid ProductCartItemDTO dto);
    
    @Operation(
        description = "Consultar o Carrinho do Usuário Logado",
        summary = "Endpoint responsável por receber a requisição de Consultar o Carrinho do Usuário Logado",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
        }
    )
    ResponseEntity<CartDTO> getMyCart();

    @Operation(
        description = "Remover um Produto do Carrinho",
        summary = "Endpoint responsável por receber a requisição de Remover um Produto do Carrinho",
        responses = {
            @ApiResponse(description = "Produto removido do Carrinho",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "O Produto não existe ou ele não está no Carrinho",responseCode = "404")
        }
    )
    ResponseEntity<CartDTO> removeProductFromCart(@PathVariable Long productId);


}
