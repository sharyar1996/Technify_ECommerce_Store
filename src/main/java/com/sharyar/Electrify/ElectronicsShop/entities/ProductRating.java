package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;
    @ManyToOne
    private Product product;
    private Double rating;

    public ProductRating(User user, Double productRating, Product product) {
        this.user = user;
        this.rating = productRating;
        this.product = product;
    }

    @Override
    public boolean equals(Object o)
    {
        ProductRating rating= null;
        if( o instanceof ProductRating)
        {
            rating = (ProductRating) o;
        }
        if(rating.getUser() == this.user && rating.product == this.product )
        {
            return true;
        }
        return false;
    }
}
