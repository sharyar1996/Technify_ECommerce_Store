package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String orderId;
    // PENDING , DISPATCHED
    private String orderStatus;
    // NOT-PAID , PAID
    private boolean paymentStatus;
    private double orderBill;
    private LocalDate orderedDate;
    @ManyToOne(cascade = CascadeType.PERSIST , fetch = FetchType.EAGER)
    private User user;
    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems = new LinkedHashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    private BillingAddress billingAddress;

}
