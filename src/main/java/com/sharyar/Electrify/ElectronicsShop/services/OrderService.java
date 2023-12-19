package com.sharyar.Electrify.ElectronicsShop.services;

import com.sharyar.Electrify.ElectronicsShop.dto.OrderDto;

import java.util.List;

public interface OrderService {

    public OrderDto proceedToCheckout();
    public void confirmAndShipToAddress();
    public List<OrderDto> getAllOrdersByAUser(String userId);
    public List<OrderDto> getMyOrderHistory();

}
