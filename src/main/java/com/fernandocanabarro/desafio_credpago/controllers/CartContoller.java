package com.fernandocanabarro.desafio_credpago.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.desafio_credpago.dtos.CartDTO;
import com.fernandocanabarro.desafio_credpago.dtos.ProductCartItemDTO;
import com.fernandocanabarro.desafio_credpago.services.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/store/api/v1/carts")
public class CartContoller {

    @Autowired
    private CartService cartService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<CartDTO> addProductToCart(@RequestBody @Valid ProductCartItemDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addProductToCart(dto));
    }

    @GetMapping("/myCart")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<CartDTO> getMyCart(){
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<CartDTO> removeProductFromCart(@PathVariable Long productId){
        return ResponseEntity.ok(cartService.removeProductFromCart(productId));
    }
}
