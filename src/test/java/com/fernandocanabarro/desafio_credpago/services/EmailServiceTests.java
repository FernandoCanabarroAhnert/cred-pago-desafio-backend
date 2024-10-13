package com.fernandocanabarro.desafio_credpago.services;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fernandocanabarro.desafio_credpago.services.exceptions.EmailException;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {

    @InjectMocks
    private EmailService emailService;
    @Mock
    private SendGrid sendGrid;
    @Mock
    private SpringTemplateEngine springTemplateEngine;

    private Response response;
    private String username,email,code;

    @BeforeEach
    public void setup() throws Exception{
        response = new Response();
        username = "username";
        email = "email";
        code = "12345";
    }

    @Test
    public void sendEmailShouldThrowNoExceptionWhenStatusCodeIs200() throws IOException{
        response.setStatusCode(200);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        assertThatCode(() -> emailService.sendEmail(username,email,code)).doesNotThrowAnyException();
    }

    @Test
    public void sendEmailShouldThrowEmailExceptionWhenStatusCodeIs400() throws IOException{
        response.setStatusCode(400);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        assertThatThrownBy(() -> emailService.sendEmail(username, email, code)).isInstanceOf(EmailException.class);
    }

    @Test
    public void sendEmailShouldThrowEmailExceptionWhenIOExceptionIsThrown() throws IOException{
        response.setStatusCode(400);
        when(sendGrid.api(any(Request.class))).thenThrow(IOException.class);

        assertThatThrownBy(() -> emailService.sendEmail(username, email, code)).isInstanceOf(EmailException.class);
    }
}
