package com.fernandocanabarro.desafio_credpago.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fernandocanabarro.desafio_credpago.dtos.TransactionHistoryDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionResponseDTO;
import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.CreditCard;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.entities.Transaction;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.factories.CartFactory;
import com.fernandocanabarro.desafio_credpago.factories.CreditCardFactory;
import com.fernandocanabarro.desafio_credpago.factories.ProductFactory;
import com.fernandocanabarro.desafio_credpago.factories.TransactionFactory;
import com.fernandocanabarro.desafio_credpago.factories.UserFactory;
import com.fernandocanabarro.desafio_credpago.projections.TransactionHistoryProjection;
import com.fernandocanabarro.desafio_credpago.repositories.CartRepository;
import com.fernandocanabarro.desafio_credpago.repositories.CreditCardRepository;
import com.fernandocanabarro.desafio_credpago.repositories.TransactionRepository;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ForbiddenException;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AuthService authService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private UserRepository userRepository;

    private Long existingId,nonExistingId;
    private User user;
    private Cart cart;
    private CreditCard creditCard;
    private ProductCartItem productCartItem;
    private Transaction transaction;
    private TransactionRequestDTO transactionRequestDTO;
    private TransactionHistoryProjection transactionHistoryProjection;

    @BeforeEach
    public void setup() throws Exception{
        existingId = 1L;
        nonExistingId = 2L;
        user = UserFactory.getUser();
        cart = CartFactory.getCart();
        creditCard = CreditCardFactory.getCreditCard();
        productCartItem = ProductFactory.getProductCartItem();
        cart.addProduct(productCartItem);
        user.getCarts().add(cart);
        transaction = TransactionFactory.geTransaction();
        transactionRequestDTO = new TransactionRequestDTO(existingId);

        transactionHistoryProjection = new TransactionHistoryProjection() {

            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public Long getUserId() {
                return 1L;
            }

            @Override
            public String getCardNumber() {
                return "1234123412341234";
            }

            @Override
            public Integer getTotalToPay() {
                return 10;
            }

            @Override
            public LocalDateTime getMoment() {
                return LocalDateTime.now();
            }
            
        };
    }

    @Test
    public void buyShouldReturnTransactionResponseDTOWhenCartIsNotEmptyAndCreditCardIsValid(){
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.findById(existingId)).thenReturn(Optional.of(creditCard));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        TransactionResponseDTO response = transactionService.buy(transactionRequestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getTotalToPay()).isEqualTo(10);
        assertThat(response.getProducts().getFirst().getAlbum()).isEqualTo("album");
        assertThat(response.getProducts().getFirst().getArtist()).isEqualTo("artist");
        assertThat(response.getProducts().getFirst().getThumb()).isEqualTo("thumb");
        assertThat(response.getProducts().getFirst().getPrice()).isEqualTo(10);
    }

    @Test
    public void buyShouldThrowForbiddenExceptionWhenCartIsEmpty(){
        user.getCurrentCart().getProducts().clear();
        when(authService.getConnectedUser()).thenReturn(user);

        assertThatThrownBy(() -> transactionService.buy(transactionRequestDTO)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void buyShouldThrowForbiddenExceptionWhenConnectedUserIsNotTheOwnerOfTheCreditCart(){
        user.setId(2L);
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.findById(existingId)).thenReturn(Optional.of(creditCard));

        assertThatThrownBy(() -> transactionService.buy(transactionRequestDTO)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    public void getMyTransactionsShouldReturnListOfTransactionResponseDTO(){
        user.getTransactions().add(transaction);
        when(authService.getConnectedUser()).thenReturn(user);

        List<TransactionResponseDTO> response = transactionService.getMyTransactions();

        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getUserId()).isEqualTo(user.getId());
        assertThat(response.getFirst().getTotalToPay()).isEqualTo(10);
        assertThat(response.getFirst().getProducts().getFirst().getAlbum()).isEqualTo("album");
        assertThat(response.getFirst().getProducts().getFirst().getArtist()).isEqualTo("artist");
        assertThat(response.getFirst().getProducts().getFirst().getThumb()).isEqualTo("thumb");
        assertThat(response.getFirst().getProducts().getFirst().getPrice()).isEqualTo(10);
    }

    @Test
    public void getTransactionByIdShouldReturnTransactionResponseDTOWhenTransactionExists(){
        when(transactionRepository.getReferenceById(existingId)).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.getTransactionById(existingId);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(user.getId());
        assertThat(response.getTotalToPay()).isEqualTo(10);
        assertThat(response.getProducts().getFirst().getAlbum()).isEqualTo("album");
        assertThat(response.getProducts().getFirst().getArtist()).isEqualTo("artist");
        assertThat(response.getProducts().getFirst().getThumb()).isEqualTo("thumb");
        assertThat(response.getProducts().getFirst().getPrice()).isEqualTo(10);
    }

    @Test
    public void getTransactionByIdShouldThrowResourceNotFoundExceptionWhenTransactionDoesNotExist(){
        when(transactionRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThatThrownBy(() -> transactionService.getTransactionById(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void getTransactionHistoryShouldReturnListOfTransactionHistoryDTO(){
        when(transactionRepository.getTransactionHistory()).thenReturn(List.of(transactionHistoryProjection));

        List<TransactionHistoryDTO> response = transactionService.getTransactionsHistory();

        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getClientId()).isEqualTo(1L);
        assertThat(response.getFirst().getTransactionId()).isEqualTo(1L);
        assertThat(response.getFirst().getValue()).isEqualTo(10);
        assertThat(response.getFirst().getCardNumber()).isEqualTo("**** **** **** 1234");
    }

    @Test
    public void getTransactionHistoryByUserIdShouldReturnListOfTransactionHistoryDTOWhenUserExists(){
        when(userRepository.findById(existingId)).thenReturn(Optional.of(user));
        when(transactionRepository.getTransactionHistoryByUserId(existingId)).thenReturn(List.of(transactionHistoryProjection));

        List<TransactionHistoryDTO> response = transactionService.getTransactionsHistoryByUserId(existingId);

        assertThat(response).isNotEmpty();
        assertThat(response.getFirst().getClientId()).isEqualTo(1L);
        assertThat(response.getFirst().getTransactionId()).isEqualTo(1L);
        assertThat(response.getFirst().getValue()).isEqualTo(10);
        assertThat(response.getFirst().getCardNumber()).isEqualTo("**** **** **** 1234");
    }

    @Test
    public void getTransactionHistoryByUserIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist(){
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> transactionService.getTransactionsHistoryByUserId(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }
}
