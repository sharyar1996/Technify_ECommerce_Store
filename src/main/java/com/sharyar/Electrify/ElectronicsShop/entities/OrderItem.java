package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderItem {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int count = 1;
    private double itemBill;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Product product;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Order order;

}
