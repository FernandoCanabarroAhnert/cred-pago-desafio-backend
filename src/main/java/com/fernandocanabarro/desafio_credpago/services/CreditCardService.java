package com.fernandocanabarro.desafio_credpago.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.time.YearMonth;

import com.fernandocanabarro.desafio_credpago.dtos.CreditCardRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.CreditCardResponseDTO;
import com.fernandocanabarro.desafio_credpago.entities.CreditCard;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.repositories.CreditCardRepository;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/yy");

    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Transactional
    public CreditCardResponseDTO addCard(CreditCardRequestDTO dto){
        CreditCard creditCard = new CreditCard();
        toEntity(creditCard,dto);
        User user = authService.getConnectedUser();
        creditCard.setUser(user);
        creditCard.setTransactions(new ArrayList<>(Arrays.asList()));
        creditCard = creditCardRepository.save(creditCard);
        user.addCreditCard(creditCard);
        userRepository.save(user);
        return new CreditCardResponseDTO(creditCard);
    }

    private void toEntity(CreditCard creditCard, CreditCardRequestDTO dto) {
        creditCard.setCardNumber(dto.getCardNumber());
        creditCard.setHolderName(dto.getHolderName());
        creditCard.setCvv(Integer.parseInt(dto.getCvv()));
        YearMonth expirationDate = YearMonth.parse(dto.getExpirationDate(), CreditCardService.dtf);
        creditCard.setExpirationDate(expirationDate.getMonth().getValue() + "/" + expirationDate.getYear());
    }

    @Transactional(readOnly = true)
    public List<CreditCardResponseDTO> getMyCreditCards(){
        User user = authService.getConnectedUser();
        return creditCardRepository.findByUser(user).stream().map(CreditCardResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public List<CreditCardResponseDTO> getCreditCardsByUserId(Long id){
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id));
        return creditCardRepository.findByUser(user).stream().map(CreditCardResponseDTO::new).toList();
    }

}
