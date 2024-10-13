package com.fernandocanabarro.desafio_credpago.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_credpago.dtos.TransactionHistoryDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionRequestDTO;
import com.fernandocanabarro.desafio_credpago.dtos.TransactionResponseDTO;
import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.CreditCard;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.entities.Transaction;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.repositories.CartRepository;
import com.fernandocanabarro.desafio_credpago.repositories.CreditCardRepository;
import com.fernandocanabarro.desafio_credpago.repositories.TransactionRepository;
import com.fernandocanabarro.desafio_credpago.repositories.UserRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ForbiddenException;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AuthService authService;
    private final CartRepository cartRepository;
    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponseDTO buy(TransactionRequestDTO dto){
        User user = authService.getConnectedUser();
        Cart cart = user.getCurrentCart();
        if (cart.getProducts().isEmpty()) {
            throw new ForbiddenException("Não é possível efetuar a compra pois o carrinho está vazio");
        }
        CreditCard creditCard = creditCardRepository.findById(dto.getCreditCardId())
            .orElseThrow(() -> new ResourceNotFoundException(dto.getCreditCardId()));
        if (!creditCard.getUser().getId().equals(user.getId())){
            throw new ForbiddenException("Este Cartão de Crédito não lhe pertence");
        }
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCreditCard(creditCard);
        transaction.setMoment(LocalDateTime.now());

        List<ProductCartItem> productCartItems = new ArrayList<>();
        int totalToPay = 0;
        for (ProductCartItem item : cart.getProducts()) {
            item.setTransaction(transaction);
            totalToPay += item.getPrice() * item.getQuantity();
            productCartItems.add(item);
        }

        transaction.setProducts(productCartItems);
        transaction.setTotalToPay(totalToPay);

        transactionRepository.save(transaction);

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setProducts(new ArrayList<>(Arrays.asList()));
        cartRepository.save(newCart);
        
        return new TransactionResponseDTO(transaction);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions")
    public List<TransactionResponseDTO> getMyTransactions(){
        User user = authService.getConnectedUser();
        return user.getTransactions().stream().map(TransactionResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions",key = "#id")
    public TransactionResponseDTO getTransactionById(Long id){
        try{
            Transaction transaction = transactionRepository.getReferenceById(id);
            authService.validateSelfOrAdmin(transaction.getUser().getId());
            return new TransactionResponseDTO(transaction);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions-history")
    public List<TransactionHistoryDTO> getTransactionsHistory(){
        return transactionRepository.getTransactionHistory().stream().map(TransactionHistoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "transactions-history",key = "#userId")
    public List<TransactionHistoryDTO> getTransactionsHistoryByUserId(Long userId){
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(userId));
        return transactionRepository.getTransactionHistoryByUserId(userId).stream().map(TransactionHistoryDTO::new).toList();
    }
}
