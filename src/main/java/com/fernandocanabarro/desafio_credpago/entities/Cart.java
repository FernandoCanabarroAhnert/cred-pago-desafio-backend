package com.fernandocanabarro.desafio_credpago.entities;

import java.util.ArrayList;
import java.util.List;

import com.fernandocanabarro.desafio_credpago.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "id.cart",fetch = FetchType.LAZY)
    private List<ProductCartItem> products = new ArrayList<>();

    public void addProduct(ProductCartItem productCartItem){
        products.add(productCartItem);
    }

    public void removeProduct(Product product){
        boolean isRemoved = products.removeIf(productCartItem -> productCartItem.getProduct().getId().equals(product.getId()));
        if (!isRemoved) {
            throw new ResourceNotFoundException("O carrinho não contém o produto especificado");
        }
    }

}
