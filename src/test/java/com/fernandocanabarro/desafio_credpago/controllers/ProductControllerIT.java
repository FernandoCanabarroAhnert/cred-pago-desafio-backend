package com.fernandocanabarro.desafio_credpago.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.fernandocanabarro.desafio_credpago.dtos.ProductDTO;
import com.fernandocanabarro.desafio_credpago.utils.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:/import_data.sql",executionPhase = ExecutionPhase.BEFORE_TEST_CLASS)
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId,nonExistingId;
    private String userEmail,userPassword;
    private String adminEmail,adminPassword;
    private String bearerTokenUser,bearerTokenAdmin;
    private ProductDTO productDTO;

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
        productDTO = new ProductDTO(null, "artist", "year", "album", 10, "thumb");
    }

    @Test
    public void findAllShouldReturnHttpStatus200() throws Exception{
        mockMvc.perform(get("/store/api/v1/products")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].productId").value(existingId))
            .andExpect(jsonPath("$[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$[0].year").value("1967"))
            .andExpect(jsonPath("$[0].price").value(150));
    }

    @Test
    public void findAllPagedShouldReturnHttpStatus200() throws Exception{
        mockMvc.perform(get("/store/api/v1/products/paged")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].productId").value(existingId))
            .andExpect(jsonPath("$.content[0].artist").value("The Beatles"))
            .andExpect(jsonPath("$.content[0].year").value("1967"))
            .andExpect(jsonPath("$.content[0].price").value(150));
    }

    @Test
    public void findByIdShouldReturnHttpStatus200WhenProductExists() throws Exception{
        mockMvc.perform(get("/store/api/v1/products/{id}",existingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.productId").value(existingId))
            .andExpect(jsonPath("$.artist").value("The Beatles"))
            .andExpect(jsonPath("$.year").value("1967"))
            .andExpect(jsonPath("$.price").value(150));
    }

    @Test
    public void findByIdShouldReturnHttpStatus404WhenProductDoesNotExist() throws Exception{
        mockMvc.perform(get("/store/api/v1/products/{id}",nonExistingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void createShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/products")
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void createShouldReturnHttpStatus403WhenCommomUserIsLogged() throws Exception{
        mockMvc.perform(put("/store/api/v1/products/{id}",nonExistingId)
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void createShouldReturnHttpStatus201WhenAdminUserIsLogged() throws Exception{
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.album").value("album"))
            .andExpect(jsonPath("$.artist").value("artist"))
            .andExpect(jsonPath("$.thumb").value("thumb"))
            .andExpect(jsonPath("$.year").value("year"))
            .andExpect(jsonPath("$.price").value(10));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenAlbumIsBlank() throws Exception{
        productDTO.setAlbum("");
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("album"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenArtistIsBlank() throws Exception{
        productDTO.setArtist("");
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("artist"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenThumbIsBlank() throws Exception{
        productDTO.setThumb("");
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("thumb"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenYearIsBlank() throws Exception{
        productDTO.setYear("");
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("year"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenPriceIsNull() throws Exception{
        productDTO.setPrice(null);
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void createShouldReturnHttpStatus422WhenPriceIsNegativeOrZero() throws Exception{
        productDTO.setPrice(-1);
        mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("Valor deve ser positivo"));
    }

    @Test
    public void updateShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void updateShouldReturnHttpStatus403WhenCommomUserIsLogged() throws Exception{
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void updateShouldReturnHttpStatus404WhenCommomUserIsLogged() throws Exception{
        mockMvc.perform(put("/store/api/v1/products/{id}",nonExistingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnHttpStatus201WhenAdminUserIsLogged() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.album").value("album"))
            .andExpect(jsonPath("$.artist").value("artist"))
            .andExpect(jsonPath("$.thumb").value("thumb"))
            .andExpect(jsonPath("$.year").value("year"))
            .andExpect(jsonPath("$.price").value(10));
        String result = resultActions.andReturn().getResponse().getContentAsString();
        Long createdId = Long.parseLong(JsonPath.parse(result).read("productId").toString());

        ProductDTO updateDTO = new ProductDTO(null, "updated", "updated", "updated", 20, "updated");

        mockMvc.perform(put("/store/api/v1/products/{id}",createdId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(updateDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.album").value("updated"))
            .andExpect(jsonPath("$.artist").value("updated"))
            .andExpect(jsonPath("$.thumb").value("updated"))
            .andExpect(jsonPath("$.year").value("updated"))
            .andExpect(jsonPath("$.price").value(20));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenAlbumIsBlank() throws Exception{
        productDTO.setAlbum("");
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("album"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenArtistIsBlank() throws Exception{
        productDTO.setArtist("");
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("artist"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenThumbIsBlank() throws Exception{
        productDTO.setThumb("");
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("thumb"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenYearIsBlank() throws Exception{
        productDTO.setYear("");
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("year"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenPriceIsNull() throws Exception{
        productDTO.setPrice(null);
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("Campo Requerido"));
    }

    @Test
    public void updateShouldReturnHttpStatus422WhenPriceIsNegativeOrZero() throws Exception{
        productDTO.setPrice(-1);
        mockMvc.perform(put("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
            .andExpect(jsonPath("$.errors[0].message").value("Valor deve ser positivo"));
    }

    @Test
    public void deleteShouldReturnHttpStatus401WhenNoUserIsLogged() throws Exception{
        mockMvc.perform(delete("/store/api/v1/products/{id}",existingId)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void deleteShouldReturnHttpStatus403WhenCommomUserIsLogged() throws Exception{
        mockMvc.perform(delete("/store/api/v1/products/{id}",existingId)
            .header("Authorization","Bearer " + bearerTokenUser)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    public void deleteShouldReturnHttpStatus404WhenProductDoesNotExist() throws Exception{
        mockMvc.perform(delete("/store/api/v1/products/{id}",nonExistingId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnHttpStatus204WhenProductExists() throws Exception{
        ResultActions resultActions = mockMvc.perform(post("/store/api/v1/products")
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.album").value("album"))
            .andExpect(jsonPath("$.artist").value("artist"))
            .andExpect(jsonPath("$.thumb").value("thumb"))
            .andExpect(jsonPath("$.year").value("year"))
            .andExpect(jsonPath("$.price").value(10));
        String result = resultActions.andReturn().getResponse().getContentAsString();
        Long createdId = Long.parseLong(JsonPath.parse(result).read("productId").toString());

        mockMvc.perform(delete("/store/api/v1/products/{id}",createdId)
            .header("Authorization","Bearer " + bearerTokenAdmin)
            .content(objectMapper.writeValueAsString(productDTO))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }
}
