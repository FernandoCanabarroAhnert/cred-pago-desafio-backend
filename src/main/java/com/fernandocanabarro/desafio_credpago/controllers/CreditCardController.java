package com.fernandocanabarro.desafio_credpago.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.desafio_credpago.dtos.CreditCardRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.CreditCardResponseDTO;
import com.fernandocanabarro.desafio_credpago.services.CreditCardService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/store/api/v1/creditCards")
public class CreditCardController {

    @Autowired
    private CreditCardService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<CreditCardResponseDTO> addCreditCard(@RequestBody @Valid CreditCardRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addCard(dto));
    }

    @GetMapping("/myCards")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<List<CreditCardResponseDTO>> findMyCards(){
        return ResponseEntity.ok(service.getMyCreditCards());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CreditCardResponseDTO>> findCreditCardsByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(service.getCreditCardsByUserId(userId));
    }
}
