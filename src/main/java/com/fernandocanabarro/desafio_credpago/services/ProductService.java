package com.fernandocanabarro.desafio_credpago.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.desafio_credpago.dtos.ProductDTO;
import com.fernandocanabarro.desafio_credpago.entities.Product;
import com.fernandocanabarro.desafio_credpago.entities.ProductCartItem;
import com.fernandocanabarro.desafio_credpago.repositories.ProductCartItemRepository;
import com.fernandocanabarro.desafio_credpago.repositories.ProductRepository;
import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCartItemRepository productCartItemRepository;

    @Cacheable(value = "products")
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll().stream().map(ProductDTO::new).toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#page + '-' + #linesPerPage + '-' + #direction + '-' + #orderBy")
    public Page<ProductDTO> findAllPaged(String page, String linesPerPage, String direction, String orderBy) {
        PageRequest pageRequest = PageRequest.of(
                Integer.parseInt(page),
                Integer.parseInt(linesPerPage),
                org.springframework.data.domain.Sort.Direction.valueOf(direction),
                orderBy);
        return productRepository.findAll(pageRequest).map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductDTO findById(Long id) {
        return productRepository.findById(id)
                .map(ProductDTO::new)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO create(ProductDTO dto) {
        Product product = new Product();
        toEntity(product, dto);
        product.setCarts(new ArrayList<>(Arrays.asList()));
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    private void toEntity(Product product, ProductDTO dto) {
        product.setArtist(dto.getArtist());
        product.setAlbum(dto.getAlbum());
        product.setYear(dto.getYear());
        product.setPrice(dto.getPrice());
        product.setThumb(dto.getThumb());
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product product = productRepository.getReferenceById(id);
            toEntity(product, dto);
            product = productRepository.save(product);
            return new ProductDTO(product);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        Product product = productRepository.getReferenceById(id);
        for (ProductCartItem x : product.getCarts()) {
            productCartItemRepository.delete(x);
        }
        productRepository.delete(product);
    }
}
