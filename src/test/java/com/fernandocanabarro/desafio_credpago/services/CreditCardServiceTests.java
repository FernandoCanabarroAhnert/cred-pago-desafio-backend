package com.fernandocanabarro.desafio_credpago.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fernandocanabarro.desafio_credpago.dtos.CreditCardRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.CreditCardResponseDTO;
import com.fernandocanabarro.desafio_credpago.entities.CreditCard;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.factories.CreditCardFactory;
import com.fernandocanabarro.desafio_credpago.factories.UserFactory;
import com.fernandocanabarro.desafio_credpago.repositories.CreditCardRepository;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceTests {

    @InjectMocks
    private CreditCardService creditCardService;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthService authService;

    private User user;
    private CreditCardRequestDTO creditCardRequestDTO;
    private CreditCard creditCard;
    private Long existingId,nonExistingId;

    @BeforeEach
    public void setup() throws Exception{
        user = UserFactory.getUser();
        creditCardRequestDTO = new CreditCardRequestDTO("name", "1234123412341234", "123","12/24");
        creditCard = CreditCardFactory.getCreditCard();
        existingId = 1L;
        nonExistingId = 2L;

    }

    @Test
    public void addCardShouldReturnCreditCardResponseDTO(){
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);
        when(userRepository.save(any(User.class))).thenReturn(user);

        CreditCardResponseDTO response = creditCardService.addCard(creditCardRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getHolderName()).isEqualTo("name");
        assertThat(response.getCardNumber()).isEqualTo("**** **** **** 1234");
        assertThat(response.getExpirationDate()).isEqualTo("12/24");
    }
    
    @Test
    public void getMyCreditCardsShouldReturnListOfCreditCardResponseDTO(){
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.findByUser(any(User.class))).thenReturn(List.of(creditCard));

        List<CreditCardResponseDTO> response = creditCardService.getMyCreditCards();

        assertThat(response).isNotNull();
        assertThat(response.get(0).getHolderName()).isEqualTo("name");
        assertThat(response.get(0).getCardNumber()).isEqualTo("**** **** **** 1234");
        assertThat(response.get(0).getExpirationDate()).isEqualTo("12/24");
    }

    @Test
    public void getCreditCardsByUserIdShouldReturnListOfCreditCardResponseDTOWhenUserExists(){
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(creditCardRepository.findByUser(any(User.class))).thenReturn(List.of(creditCard));

        List<CreditCardResponseDTO> response = creditCardService.getCreditCardsByUserId(existingId);
        
        assertThat(response).isNotNull();
        assertThat(response.get(0).getHolderName()).isEqualTo("name");
        assertThat(response.get(0).getCardNumber()).isEqualTo("**** **** **** 1234");
        assertThat(response.get(0).getExpirationDate()).isEqualTo("12/24");
    }

    @Test
    public void getCreditCardsByUserIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist(){
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> creditCardService.getCreditCardsByUserId(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }
}
