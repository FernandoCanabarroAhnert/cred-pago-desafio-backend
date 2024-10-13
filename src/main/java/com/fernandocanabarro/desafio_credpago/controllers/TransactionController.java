package com.fernandocanabarro.desafio_credpago.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fernandocanabarro.desafio_credpago.dtos.TransactionHistoryDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionResponseDTO;
import com.fernandocanabarro.desafio_credpago.services.TransactionService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/store/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PostMapping("/buy")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> buy(@RequestBody @Valid TransactionRequestDTO dto){
        TransactionResponseDTO response = service.buy(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(response.getId()).toUri();
            return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/myTransactions")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions(){
        return ResponseEntity.ok(service.getMyTransactions());
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<TransactionResponseDTO> findTransactionById(@PathVariable Long transactionId){
        return ResponseEntity.ok(service.getTransactionById(transactionId));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionsHistory(){
        return ResponseEntity.ok(service.getTransactionsHistory());
    }

    @GetMapping("/history/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionsHistoryByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(service.getTransactionsHistoryByUserId(userId));
    }
}
