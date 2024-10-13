package com.fernandocanabarro.desafio_credpago.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_credpago.dtos.CartDTO;
import com.fernandocanabarro.desafio_credpago.dtos.ProductCartItemDTO;
import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.Product;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.entities.pk.ProductCartItemPK;
import com.fernandocanabarro.desafio_credpago.repositories.CartRepository;
import com.fernandocanabarro.desafio_credpago.repositories.ProductCartItemRepository;
import com.fernandocanabarro.desafio_credpago.repositories.ProductRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final AuthService authService;
    private final CartRepository cartRepository;
    private final ProductCartItemRepository productCartItemRepository;

    @Transactional
    public CartDTO addProductToCart(ProductCartItemDTO dto){
        try{
            Product product = productRepository.getReferenceById(dto.getProductId());
            User user = authService.getConnectedUser();
            Cart cart = user.getCurrentCart();

            ProductCartItem productCartItem = new ProductCartItem(cart, product, dto.getQuantity(), product.getPrice());
            productCartItem = productCartItemRepository.save(productCartItem);

            cart.addProduct(productCartItem);
            cart = cartRepository.save(cart);
            return new CartDTO(cart);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException(dto.getProductId());
        }
    }

    @Transactional(readOnly = true)
    public CartDTO getMyCart(){
        User user = authService.getConnectedUser();
        return new CartDTO(user.getCurrentCart());
    }

    @Transactional
    public CartDTO removeProductFromCart(Long id){
        try{
            Product product = productRepository.getReferenceById(id);
            User user = authService.getConnectedUser();
            Cart cart = user.getCurrentCart();
            cart.removeProduct(product);
            cart = cartRepository.save(cart);
            productCartItemRepository.deleteById(new ProductCartItemPK(cart, product));
            return new CartDTO(cart);
        }
        catch (EntityNotFoundException e){
            throw new ResourceNotFoundException(id);
        }
    }
}
