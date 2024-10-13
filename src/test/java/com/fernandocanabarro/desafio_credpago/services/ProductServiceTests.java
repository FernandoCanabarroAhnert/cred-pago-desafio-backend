package com.fernandocanabarro.desafio_credpago.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.desafio_credpago.dtos.ProductDTO;
import com.fernandocanabarro.desafio_credpago.entities.Product;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.factories.ProductFactory;
import com.fernandocanabarro.desafio_credpago.repositories.ProductCartItemRepository;
import com.fernandocanabarro.desafio_credpago.repositories.ProductRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductCartItemRepository productCartItemRepository;

    private Long existingId,nonExistingId;
    private String page, linesPerPage, direction, orderBy;
    private Product product;
    private Page<Product> pageResponse;
    private ProductDTO productDTO;

    @BeforeEach
    public void setup() throws Exception{
        product = ProductFactory.getProduct();
        existingId = 1L;
        nonExistingId = 2L;
        page = "0";
        linesPerPage = "10";
        direction = "ASC";
        orderBy = "id";
        pageResponse = new PageImpl<>(List.of(product));
        productDTO = new ProductDTO(product);
    }

    @Test
    public void findByAllShouldReturnListOfProductDTO(){
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDTO> response = productService.findAll();

        assertThat(response).isNotEmpty();
        assertThat(response.get(0).getArtist()).isEqualTo("artist");
        assertThat(response.get(0).getThumb()).isEqualTo("thumb");
        assertThat(response.get(0).getAlbum()).isEqualTo("album");
    }

    @Test
    public void findAllPagedShouldReturnPageOfProductDTO(){
        when(productRepository.findAll(any(Pageable.class))).thenReturn(pageResponse);

        Page<ProductDTO> response = productService.findAllPaged(page, linesPerPage, direction, orderBy);

        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getArtist()).isEqualTo("artist");
        assertThat(response.getContent().get(0).getThumb()).isEqualTo("thumb");
        assertThat(response.getContent().get(0).getAlbum()).isEqualTo("album");
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists(){
        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));

        ProductDTO response = productService.findById(existingId);

        assertThat(response).isNotNull();
        assertThat(response.getAlbum()).isEqualTo("album");
        assertThat(response.getThumb()).isEqualTo("thumb");
        assertThat(response.getArtist()).isEqualTo("artist");
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createShouldReturnProductDTO(){
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO response = productService.create(productDTO);

        assertThat(response).isNotNull();
        assertThat(response.getAlbum()).isEqualTo("album");
        assertThat(response.getThumb()).isEqualTo("thumb");
        assertThat(response.getArtist()).isEqualTo("artist");
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists(){
        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO response = productService.update(existingId, productDTO);

        assertThat(response).isNotNull();
        assertThat(response.getAlbum()).isEqualTo("album");
        assertThat(response.getThumb()).isEqualTo("thumb");
        assertThat(response.getArtist()).isEqualTo("artist");
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThatThrownBy(() -> productService.update(nonExistingId,productDTO)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void deleteShouldThrowNoExceptionWhenIdExists(){
        product.getCarts().add(ProductFactory.getProductCartItem());
        when(productRepository.existsById(existingId)).thenReturn(true);
        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        doNothing().when(productCartItemRepository).delete(any(ProductCartItem.class));
        doNothing().when(productRepository).delete(any(Product.class));

        assertThatCode(() -> productService.delete(existingId)).doesNotThrowAnyException();
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        when(productRepository.existsById(nonExistingId)).thenReturn(false);

        assertThatThrownBy(() -> productService.delete(nonExistingId)).isInstanceOf(ResourceNotFoundException.class);

    }
}
