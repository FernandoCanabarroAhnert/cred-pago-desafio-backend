package com.fernandocanabarro.desafio_credpago.services.validations;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fernandocanabarro.desafio_credpago.dtos.CreditCardRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.exceptions.FieldMessage;
import com.fernandocanabarro.desafio_credpago.entities.CreditCard;
import com.fernandocanabarro.desafio_credpago.repositories.CreditCardRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditCardRequestDTOValidator implements ConstraintValidator<CreditCardRequestDTOValid,CreditCardRequestDTO>{

    @Autowired
    private CreditCardRepository creditCardRepository;

    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");

    @Override
    public void initialize(CreditCardRequestDTOValid ann){}

    @Override
    public boolean isValid(CreditCardRequestDTO dto, ConstraintValidatorContext context) {
        List<FieldMessage> errors = new ArrayList<>();

        Optional<CreditCard> creditCard = creditCardRepository.findByCardNumber(dto.getCardNumber());
        if (creditCard.isPresent()) {
            errors.add(new FieldMessage("cardNumber", "Um cartão com este número já está em uso"));
        }

        String regex = "^(0[1-9]|1[0-2])\\/\\d{2}$";

        if (!dto.getExpirationDate().matches(regex)) {
            errors.add(new FieldMessage("expirationDate","Data deve estar em formato válido: mm/yy"));
        }

        YearMonth expirationDate = YearMonth.parse(dto.getExpirationDate(), dtf);

        if (!expirationDate.isAfter(YearMonth.now())) {
            errors.add(new FieldMessage("expirationDate", "Cartão de Crédito já expirou"));
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
