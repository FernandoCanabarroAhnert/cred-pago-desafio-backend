package com.fernandocanabarro.desafio_credpago.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fernandocanabarro.desafio_credpago.dtos.ProductCartItemDTO;
import com.fernandocanabarro.desafio_credpago.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:/import_data.sql",executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class CartControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId,nonExistingId;
    private String userEmail,userPassword;
    private String adminEmail,adminPassword;
    private String bearerTokenUser,bearerTokenAdmin;
    private ProductCartItemDTO productCartItemDTO;

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
        productCartItemDTO = new ProductCartItemDTO();
        productCartItemDTO.setProductId(1L);
        productCartItemDTO.setQuantity(1);
    }

    @Test
    public void addProductToCartShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/carts")
            .content(objectMapper.writeValueAsString(productCartItemDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void addProductToCartShouldReturnHttpStatus201WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/carts")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productCartItemDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.clientName").value("Maria Brown"))
            .andExpect(jsonPath("$.products[0].productId").value(1L))
            .andExpect(jsonPath("$.products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$.products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.products[0].year").value("1967"));
    }

    @Test
    public void addProductToCartShouldReturnHttpStatus404WhenProductDoesNotExist() throws Exception{
        productCartItemDTO.setProductId(nonExistingId);
        mockMvc.perform(post("/store/api/v1/carts")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productCartItemDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void addProductToCartShouldReturnHttpStatus422WhenProductIdIsNull() throws Exception{
        productCartItemDTO.setProductId(null);
        mockMvc.perform(post("/store/api/v1/carts")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productCartItemDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("productId"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void addProductToCartShouldReturnHttpStatus422WhenQuantityIsNull() throws Exception{
        productCartItemDTO.setQuantity(null);
        mockMvc.perform(post("/store/api/v1/carts")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productCartItemDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("quantity"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void getMyCartShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/carts/myCart")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getMyCartShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/carts/myCart")
            .header("Authorization", "Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientId").value(2L))
            .andExpect(jsonPath("$.clientName").value("Alex Green"))
            .andExpect(jsonPath("$.products[0].productId").value(1L))
            .andExpect(jsonPath("$.products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$.products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.products[0].year").value("1967"));
    }

    @Test
    public void removeProductFromCartShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(delete("/store/api/v1/carts/{id}",existingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void removeProductFromCartShouldReturnHttpStatus404WhenProductIsNotInTheCart() throws Exception{
        mockMvc.perform(delete("/store/api/v1/carts/{id}",2L)
            .header("Authorization", "Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void removeProductFromCartShouldReturnHttpStatus404WhenProductDoesNotExist() throws Exception{
        mockMvc.perform(delete("/store/api/v1/carts/{id}",nonExistingId)
            .header("Authorization", "Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void removeProductFromCartShouldReturnHttpStatus200WhenAnyUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/carts")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productCartItemDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.clientName").value("Maria Brown"))
            .andExpect(jsonPath("$.products[0].productId").value(1L))
            .andExpect(jsonPath("$.products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$.products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.products[0].year").value("1967"));

        mockMvc.perform(delete("/store/api/v1/carts/{id}",existingId)
            .header("Authorization", "Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.clientId").value(1L))
            .andExpect(jsonPath("$.clientName").value("Maria Brown"))
            .andExpect(jsonPath("$.products").isEmpty());
    }
}
