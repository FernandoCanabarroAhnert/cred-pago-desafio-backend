package com.fernandocanabarro.desafio_credpago.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fernandocanabarro.desafio_credpago.services.exceptions.EmailException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class EmailService {

    @Autowired
    private SendGrid sendGrid;
    @Autowired
    private SpringTemplateEngine springTemplateEngine;
    private String sendgridEmail = "ahnertfernando499@gmail.com";

    @Async
    public void sendEmail(String username,String email, String code){
        Mail mail = createConfirmationEmailTemplate(username, email, code);
        Request request = new Request();
        try{
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            if (response.getStatusCode() >= 400) {
                throw new EmailException("Falha ao enviar ao e-mail");
            }
        }
        catch (IOException e){
            throw new EmailException(e.getMessage());
        }
    }

    private Mail createConfirmationEmailTemplate(String username,String email, String code){
        Map<String,Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("activation_code", code);

        Context context = new Context();
        context.setVariables(variables);
        
        String template = springTemplateEngine.process("email_confirmation", context);

        Email emailTo = new Email(email);
        Email from = new Email(sendgridEmail,"CredPago Products");
        Content content = new Content("text/html", template);

        return new Mail(from, "Ativação de Conta", emailTo, content);
    }
}
