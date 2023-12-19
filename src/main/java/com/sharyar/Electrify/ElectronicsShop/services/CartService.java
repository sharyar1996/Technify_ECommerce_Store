package com.sharyar.Electrify.ElectronicsShop.services;

import com.sharyar.Electrify.ElectronicsShop.dto.CartDto;
import com.sharyar.Electrify.ElectronicsShop.dto.CartItemRequest;
import com.sharyar.Electrify.ElectronicsShop.entities.Cart;

public interface CartService {

    CartDto addToCart(CartItemRequest itemRequest);
    CartDto deleteFromCart(int CartItemId );
    void clearCart();
    CartDto getCart();

}
