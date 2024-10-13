package com.fernandocanabarro.desafio_credpago.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fernandocanabarro.desafio_credpago.dtos.CreditCardRequestDTO;
import com.fernandocanabarro.desafio_credpago.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:/import_data.sql",executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class CreditCardControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId,nonExistingId;
    private String userEmail,userPassword;
    private String adminEmail,adminPassword;
    private String bearerTokenUser,bearerTokenAdmin;
    private CreditCardRequestDTO creditCardRequestDTO;

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine");
        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    public static void dynamicPropertySource(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    public void setup() throws Exception{
        existingId = 1L;
        nonExistingId = 999L;
        userEmail = "maria@gmail.com";
        userPassword = "12345Az@";
        adminEmail = "alex@gmail.com";
        adminPassword = "12345Az@";
        bearerTokenUser = TokenUtils.obtainAccessToken(mockMvc, objectMapper, userEmail, userPassword);
        bearerTokenAdmin = TokenUtils.obtainAccessToken(mockMvc, objectMapper, adminEmail, adminPassword);
        creditCardRequestDTO = new CreditCardRequestDTO("Alex Green", "1234432112344321", "321", "12/25");
    }

    @Test
    public void addCreditCardShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/creditCards")
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void addCreditCardShouldReturnHttpStatus201WhenAnyUserIsLogged() throws Exception{
        creditCardRequestDTO.setCardNumber("4321432112341234");
        mockMvc.perform(post("/store/api/v1/creditCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
    }

    @Test
    public void addCreditCardShouldReturnHttpStatus422WhenHolderNameIsBlank() throws Exception{
        creditCardRequestDTO.setHolderName("");
        mockMvc.perform(post("/store/api/v1/creditCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("holderName"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    
    @Test
    public void addCreditCardShouldReturnHttpStatus422WhenCardNumberIsBlank() throws Exception{
        creditCardRequestDTO.setCardNumber("");
        mockMvc.perform(post("/store/api/v1/creditCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("cardNumber"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void addCreditCardShouldReturnHttpStatus422WhenHolderCvvHasMoreThan3Characters() throws Exception{
        creditCardRequestDTO.setCvv("123456");
        mockMvc.perform(post("/store/api/v1/creditCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("cvv"))
            .andExpect(jsonPath("$.errors[0].message").value("CVV deve ter 3 caracteres"));
    }

    @Test
    public void addCreditCardShouldReturnHttpStatus422WhenHolderCvvHasLessThan3Characters() throws Exception{
        creditCardRequestDTO.setCvv("12");
        mockMvc.perform(post("/store/api/v1/creditCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("cvv"))
            .andExpect(jsonPath("$.errors[0].message").value("CVV deve ter 3 caracteres"));
    }

    @Test
    public void addCreditCardShouldReturnHttpStatus422WhenHolderCvvIsBlank() throws Exception{
        creditCardRequestDTO.setCvv("   ");
        mockMvc.perform(post("/store/api/v1/creditCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(creditCardRequestDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("cvv"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void getMyCreditCardsShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/creditCards/myCards")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getMyCreditCardsShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/creditCards/myCards")
            .header("Authorization","Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].holderName").value("Maria Brown"))
            .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 1234"))
            .andExpect(jsonPath("$[0].expirationDate").value("12/2025"));
    }

    @Test
    public void findCreditCardsByUserIdShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/creditCards/user/{id}",existingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findCreditCardsByUserIdShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/creditCards/user/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void findCreditCardsByUserIdShouldReturnHttpStatus404WhenUserDoesNotExist() throws Exception{
        mockMvc.perform(get("/store/api/v1/creditCards/user/{id}",nonExistingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void findCreditCardsByUserIdShouldReturnHttpStatus200WhenAdminIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/creditCards/user/1")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].holderName").value("Maria Brown"))
            .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 1234"))
            .andExpect(jsonPath("$[0].expirationDate").value("12/2025"));
    }
}
