package com.fernandocanabarro.desafio_credpago.entities;

import com.fernandocanabarro.desafio_credpago.entities.pk.ProductCartItemPK;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "product_cart_items")
public class ProductCartItem {

    @EmbeddedId
    private ProductCartItemPK id = new ProductCartItemPK();

    private Integer quantity;
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    public ProductCartItem(Cart cart,Product product, Integer quantity, Integer price) {
        id.setCart(cart);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = price;
    }

    public void setCart(Cart cart){
        id.setCart(cart);
    }

    public void setProduct(Product product){
        id.setProduct(product);
    }

    public Cart getCart(){
        return id.getCart();
    }

    public Product getProduct(){
        return id.getProduct();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductCartItem other = (ProductCartItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    
}
