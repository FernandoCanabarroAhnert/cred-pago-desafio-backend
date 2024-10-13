package com.fernandocanabarro.desafio_credpago.openapi;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import com.fernandocanabarro.desafio_credpago.dtos.ProductDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

public interface ProductControllerOpenAPI {

    @Operation(
        description = "Consultar todos os Produtos",
        summary = "Endpoint responsável por receber a requisição de Consultar todos os Produtos",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200")
        }
    )
    ResponseEntity<List<ProductDTO>> findAll();
    
    @Operation(
        description = "Consultar todos os Produtos Paginados",
        summary = "Endpoint responsável por receber a requisição de Consultar todos os Produtos Paginados",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200")
        }
    )
    ResponseEntity<Page<ProductDTO>> findAllPaged(
        @RequestParam(name = "page", defaultValue = "0") String page,
        @RequestParam(name = "size",defaultValue = "20") String size,
        @RequestParam(name = "sort",defaultValue = "id") String sort,
        @RequestParam(name = "direction", defaultValue = "ASC") String direction
    );

    @Operation(
        description = "Consultar Produto por Id",
        summary = "Endpoint responsável por receber a requisição de Consultar Produto por Id",
        responses = {
            @ApiResponse(description = "Consulta Realizada",responseCode = "200"),
            @ApiResponse(description = "Produto não encontrado",responseCode = "404")
        }
    )
    ResponseEntity<ProductDTO> findById(@PathVariable Long id);

    @Operation(
        description = "Criar um novo Produto",
        summary = "Endpoint responsável por receber a requisição de Criar um novo Produto",
        responses = {
            @ApiResponse(description = "Produto Criado",responseCode = "201"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Usuário sem permissão faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<ProductDTO> create(@RequestBody @Valid ProductDTO productDTO);

    @Operation(
        description = "Atualizar um Produto",
        summary = "Endpoint responsável por receber a requisição de Atualizar um Produto",
        responses = {
            @ApiResponse(description = "Produto Atualizado",responseCode = "200"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Usuário sem permissão faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Produto não encontrado",responseCode = "404"),
            @ApiResponse(description = "Algum dado da requisição está inválido",responseCode = "422")
        }
    )
    ResponseEntity<ProductDTO> update(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO);

    @Operation(
        description = "Deletar um Produto",
        summary = "Endpoint responsável por receber a requisição de Deletar um Produto",
        responses = {
            @ApiResponse(description = "Produto Deletado",responseCode = "204"),
            @ApiResponse(description = "Usuário não autenticado faz a requisição",responseCode = "401"),
            @ApiResponse(description = "Usuário sem permissão faz a requisição",responseCode = "403"),
            @ApiResponse(description = "Produto não encontrado",responseCode = "404")
        }
    )
    ResponseEntity<Void> delete(@PathVariable Long id);

}
