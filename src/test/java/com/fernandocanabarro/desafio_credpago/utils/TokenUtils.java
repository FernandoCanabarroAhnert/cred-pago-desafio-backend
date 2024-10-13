package com.fernandocanabarro.desafio_credpago.utils;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fernandocanabarro.desafio_credpago.dtos.LoginRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class TokenUtils {

    public static String obtainAccessToken(MockMvc mockMvc,ObjectMapper objectMapper,String username,String password) throws JsonProcessingException, Exception{
        ResultActions resultActions = mockMvc.perform(post("/store/api/v1/auth/login")
            .content(objectMapper.writeValueAsString(new LoginRequestDTO(username,password)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        String result = resultActions.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(result).get("accessToken").toString();
    }
}
