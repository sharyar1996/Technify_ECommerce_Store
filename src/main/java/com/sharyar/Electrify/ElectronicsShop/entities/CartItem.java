package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Cart cart;
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int count = 1;
    private double itemBill;

    private void setItemBill() {
        double productPrice = product.getPrice();
        if(product.getDiscountedPrice() >0)
        {
            productPrice = product.getDiscountedPrice();
        }
        itemBill = productPrice * count;
    }
    public double getItemBill()
    {
        return this.itemBill;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        if(this.product != null)
        {
            setItemBill();
        }
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
