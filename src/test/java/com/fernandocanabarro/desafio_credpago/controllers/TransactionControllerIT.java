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
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fernandocanabarro.desafio_credpago.dtos.ProductCartItemDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionRequestDTO;
import com.fernandocanabarro.desafio_credpago.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:/import_data.sql",executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId,nonExistingId;
    private String userEmail,userPassword;
    private String adminEmail,adminPassword;
    private String bearerTokenUser,bearerTokenAdmin;
    private TransactionRequestDTO transactionRequestDTO;
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
        transactionRequestDTO = new TransactionRequestDTO(1L);
        productCartItemDTO = new ProductCartItemDTO();
        productCartItemDTO.setProductId(1L);
        productCartItemDTO.setQuantity(1);
    }

    @Test
    public void buyShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/transactions/buy")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void buyShouldReturnHttpStatus403WhenCartIsEmpty() throws Exception{
        mockMvc.perform(post("/store/api/v1/transactions/buy")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void buyShouldReturnHttpStatus404WhenCreditCardDoesNotExist() throws Exception{
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

        transactionRequestDTO.setCreditCardId(nonExistingId);

        mockMvc.perform(post("/store/api/v1/transactions/buy")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void buyShouldReturnHttpStatus403WhenCreditCardDoesNotBelongToConnectedUser() throws Exception{
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

        transactionRequestDTO.setCreditCardId(2L);

        mockMvc.perform(post("/store/api/v1/transactions/buy")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    public void buyShouldReturnHttpStatus422WhenCreditCardIdIsNull() throws Exception {
        transactionRequestDTO.setCreditCardId(null);
        mockMvc.perform(post("/store/api/v1/transactions/buy")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("creditCardId"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void testBuyAndAllQueryEndpointsRelatedToIt() throws Exception{
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

        ResultActions userTransactionBuyResultActions = mockMvc.perform(post("/store/api/v1/transactions/buy")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.products[0].productId").value(1L))
            .andExpect(jsonPath("$.products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$.products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.products[0].year").value("1967"))
            .andExpect(jsonPath("$.totalToPay").value(150))
            .andExpect(jsonPath("$.creditCard.id").value(1L))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.userEmail").value("maria@gmail.com"));
        String response = userTransactionBuyResultActions.andReturn().getResponse().getContentAsString();
        Long transactionId = Long.parseLong(JsonPath.parse(response).read("id").toString());

        mockMvc.perform(get("/store/api/v1/transactions/myTransactions")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].products[0].productId").value(1L))
            .andExpect(jsonPath("$[0].products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$[0].products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$[0].products[0].year").value("1967"))
            .andExpect(jsonPath("$[0].totalToPay").value(150))
            .andExpect(jsonPath("$[0].creditCard.id").value(1L))
            .andExpect(jsonPath("$[0].userId").value(1L))
            .andExpect(jsonPath("$[0].userEmail").value("maria@gmail.com"));

        mockMvc.perform(get("/store/api/v1/transactions/{id}",transactionId)
            .header("Authorization", "Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.products[0].productId").value(1L))
            .andExpect(jsonPath("$.products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$.products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.products[0].year").value("1967"))
            .andExpect(jsonPath("$.totalToPay").value(150))
            .andExpect(jsonPath("$.creditCard.id").value(1L))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.userEmail").value("maria@gmail.com"));
        
        mockMvc.perform(get("/store/api/v1/transactions/history")
            .header("Authorization", "Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].transactionId").value(transactionId))
            .andExpect(jsonPath("$[0].clientId").value(1L))
            .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 1234"))
            .andExpect(jsonPath("$[0].value").value(150));

        mockMvc.perform(get("/store/api/v1/transactions/history/{userId}",1L)
            .header("Authorization", "Bearer " + bearerTokenAdmin)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].transactionId").value(transactionId))
            .andExpect(jsonPath("$[0].clientId").value(1L))
            .andExpect(jsonPath("$[0].cardNumber").value("**** **** **** 1234"))
            .andExpect(jsonPath("$[0].value").value(150));
    }

    @Test
    public void findTransactionByIdShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/transactions/{id}",existingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void findTransactionByIdShouldReturnHttpStatus403WhenTransactionDoesNotBelongToConnectedUser() throws Exception {
        transactionRequestDTO.setCreditCardId(2L);
        ResultActions adminTransactionBuyResultActions = mockMvc.perform(post("/store/api/v1/transactions/buy")
            .header("Authorization", "Bearer " + bearerTokenAdmin)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactionRequestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.products[0].productId").value(1L))
            .andExpect(jsonPath("$.products[0].album").value("Sgt. Peppers Lonely Hearts Club Band"))
            .andExpect(jsonPath("$.products[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.products[0].year").value("1967"))
            .andExpect(jsonPath("$.totalToPay").value(150))
            .andExpect(jsonPath("$.creditCard.id").value(2L))
            .andExpect(jsonPath("$.userId").value(2L))
            .andExpect(jsonPath("$.userEmail").value("alex@gmail.com"));
        String response = adminTransactionBuyResultActions.andReturn().getResponse().getContentAsString();
        Long transactionId = Long.parseLong(JsonPath.parse(response).read("id").toString());

        mockMvc.perform(get("/store/api/v1/transactions/{id}",transactionId)
            .header("Authorization", "Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getTransactionHistoryShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/transactions/history")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getTransactionHistoryShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/transactions/history")
            .header("Authorization", "Bearer " + bearerTokenUser)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getTransactionHistoryByUserIdShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/transactions/history/1")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void getTransactionHistoryByUserIdShouldReturnHttpStatus403WhenCommonUserIsLogged() throws Exception{
        mockMvc.perform(get("/store/api/v1/transactions/history/1")
            .header("Authorization", "Bearer " + bearerTokenUser)    
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void getTransactionHistoryByUserIdShouldReturnHttpStatus404UserDoesNotExist() throws Exception{
        mockMvc.perform(get("/store/api/v1/transactions/history/{id}",nonExistingId)
            .header("Authorization", "Bearer " + bearerTokenAdmin)   
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

}
