package com.fernandocanabarro.desafio_credpago.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fernandocanabarro.desafio_credpago.dtos.CartDTO;
import com.fernandocanabarro.desafio_credpago.dtos.ProductCartItemDTO;
import com.fernandocanabarro.desafio_credpago.entities.Cart;
import com.fernandocanabarro.desafio_credpago.entities.Product;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.entities.User;
import com.fernandocanabarro.desafio_credpago.factories.CartFactory;
import com.fernandocanabarro.desafio_credpago.factories.ProductFactory;
import com.fernandocanabarro.desafio_credpago.factories.UserFactory;
import com.fernandocanabarro.desafio_credpago.repositories.CartRepository;
import com.fernandocanabarro.desafio_credpago.repositories.ProductCartItemRepository;
import com.fernandocanabarro.desafio_credpago.repositories.ProductRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class CartServiceTests {

    @InjectMocks
    private CartService cartService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private AuthService authService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductCartItemRepository productCartItemRepository;

    private ProductCartItem productCartItem;
    private ProductCartItemDTO productCartItemDTO;
    private Product product;
    private User user;
    private Cart cart;
    private Long existingId,nonExistingId;

    @BeforeEach
    public void setup() throws Exception{
        productCartItemDTO = new ProductCartItemDTO(1L, "artist", "2024", "album", 10, "thumb", 1);
        product = ProductFactory.getProduct();
        user = UserFactory.getUser();
        cart = CartFactory.getCart();
        productCartItem = ProductFactory.getProductCartItem();
        existingId = 1L;
        nonExistingId = 2L;
        cart.addProduct(productCartItem);
        user.getCarts().add(cart);
    }

    @Test
    public void addProductToCartShouldReturnCartDTOWhenProductExists(){
        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(authService.getConnectedUser()).thenReturn(user);
        when(productCartItemRepository.save(any(ProductCartItem.class))).thenReturn(productCartItem);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO response = cartService.addProductToCart(productCartItemDTO);

        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(1L);
        assertThat(response.getClientId()).isEqualTo(1L);
        assertThat(response.getClientName()).isEqualTo("name");
        assertThat(response.getProducts().getFirst().getProductId()).isEqualTo(1L);
        assertThat(response.getProducts().getFirst().getAlbum()).isEqualTo("album");
        assertThat(response.getProducts().getFirst().getArtist()).isEqualTo("artist");
    }

    @Test
    public void addProductToCartShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist(){
        productCartItemDTO.setProductId(nonExistingId);
        when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThatThrownBy(() -> cartService.addProductToCart(productCartItemDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void getMyCartShouldReturnCartDTO(){
        when(authService.getConnectedUser()).thenReturn(user);

        CartDTO response = cartService.getMyCart();

        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(1L);
        assertThat(response.getClientId()).isEqualTo(1L);
        assertThat(response.getClientName()).isEqualTo("name");
        assertThat(response.getProducts().getFirst().getProductId()).isEqualTo(1L);
        assertThat(response.getProducts().getFirst().getAlbum()).isEqualTo("album");
        assertThat(response.getProducts().getFirst().getArtist()).isEqualTo("artist");
    }

    @Test
    public void removeProductFromCartShouldReturnCartDTOWhenProductExists(){
        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(authService.getConnectedUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO response = cartService.removeProductFromCart(existingId);

        assertThat(response).isNotNull();
        assertThat(response.getCartId()).isEqualTo(1L);
        assertThat(response.getClientId()).isEqualTo(1L);
        assertThat(response.getClientName()).isEqualTo("name");
        assertThat(response.getProducts()).isEmpty();
    }

    @Test
    public void removeProductFromCartShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist(){
        when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThatThrownBy(() -> cartService.removeProductFromCart(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }
}
