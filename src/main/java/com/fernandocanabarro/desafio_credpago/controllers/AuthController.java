package com.fernandocanabarro.desafio_credpago.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.desafio_credpago.dtos.AccountActivationResponse;
import com.fernandocanabarro.desafio_credpago.dtos.LoginRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.LoginResponseDTO;
import com.fernandocanabarro.desafio_credpago.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.UserDTO;
import com.fernandocanabarro.desafio_credpago.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/store/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid RegistrationRequestDTO dto){
        UserDTO response = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/activate")
    public ResponseEntity<AccountActivationResponse> activate(@RequestParam(name = "code",defaultValue = "") String code){
        return ResponseEntity.ok(authService.activateAccount(code));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto){
        return ResponseEntity.ok(authService.login(dto));
    }
}
