package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne(cascade = CascadeType.PERSIST)
    private User user;
    @OneToMany(cascade = CascadeType.ALL ,orphanRemoval = true  ,fetch = FetchType.EAGER )
    private Set<CartItem> cartItemSet = new LinkedHashSet<>();
    private LocalDate createdOn;
    private double totalBill;

    public Cart(LocalDate today , User user)
    {
        this.createdOn = today;
        this.user = user;
    }

    public void setTotalBill()
    {
        Iterator<CartItem> it = cartItemSet.iterator();
        double bill = 0.0;
        while(it.hasNext())
        {
            bill += it.next().getItemBill();
        }
        totalBill = bill;
    }

}
