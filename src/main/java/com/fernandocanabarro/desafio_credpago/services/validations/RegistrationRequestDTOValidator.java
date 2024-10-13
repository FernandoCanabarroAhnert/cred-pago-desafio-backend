package com.fernandocanabarro.desafio_credpago.services.validations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import com.fernandocanabarro.desafio_credpago.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.exceptions.FieldMessage;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegistrationRequestDTOValidator implements ConstraintValidator<RegistrationRequestDTOValid,RegistrationRequestDTO>{

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(RegistrationRequestDTOValid ann){}

    @Override
    public boolean isValid(RegistrationRequestDTO dto, ConstraintValidatorContext context) {
        List<FieldMessage> errors = new ArrayList<>();

        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent()) {
            errors.add(new FieldMessage("email", "Este e-mail já está em uso"));
        }

        String password = dto.getPassword();

        if (!Pattern.matches(".*[A-Z].*", password)) {
            errors.add(new FieldMessage("password","Senha deve conter pelo menos 1 letra maiúscula"));
        }
        if (!Pattern.matches(".*[a-z].*", password)) {
            errors.add(new FieldMessage("password","Senha deve conter pelo menos 1 letra minúscula"));
        }
        if (!Pattern.matches(".*[0-9].*", password)) {
            errors.add(new FieldMessage("password","Senha deve conter pelo menos 1 número"));
        }
        if (!Pattern.matches(".*[\\W].*", password)) {
            errors.add(new FieldMessage("password","Senha deve conter pelo menos 1 caractere especial"));
        }

        for (FieldMessage f : errors){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(f.getMessage())
                .addPropertyNode(f.getFieldName())
                .addConstraintViolation();
        }

        return errors.isEmpty();
    }

}
