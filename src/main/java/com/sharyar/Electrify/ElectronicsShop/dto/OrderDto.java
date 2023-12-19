package com.sharyar.Electrify.ElectronicsShop.dto;

import com.sharyar.Electrify.ElectronicsShop.entities.BillingAddress;
import com.sharyar.Electrify.ElectronicsShop.entities.OrderItem;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
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
public class OrderDto {
    private String orderId;
    // PENDING , DISPATCHED
    private String orderStatus;
    // NOT-PAID , PAID
    private boolean paymentStatus;
    private double orderBill;
    private LocalDate orderedDate;
    private BillingAddressDto billingAddress;
    private String user;

    public void setUser(User user)
    {
        this.user = user.getUserName();
    }

}
