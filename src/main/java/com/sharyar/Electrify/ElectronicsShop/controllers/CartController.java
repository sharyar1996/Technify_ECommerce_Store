package com.sharyar.Electrify.ElectronicsShop.controllers;

import com.sharyar.Electrify.ElectronicsShop.dto.ApiResponseMessage;
import com.sharyar.Electrify.ElectronicsShop.dto.CartDto;
import com.sharyar.Electrify.ElectronicsShop.dto.CartItemRequest;
import com.sharyar.Electrify.ElectronicsShop.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping("/addToCart")
    @Operation(
            summary = "Adds a cart-item to the currently " +
                    "logged-in user's cart. (One USER can only have one Cart"
    )
    public ResponseEntity<CartDto> addToCart(@RequestBody @Valid CartItemRequest addRequest)
    {
        CartDto updatedCartDto =  cartService.addToCart(addRequest);
        return  new ResponseEntity<>(updatedCartDto , HttpStatus.OK);
    }

    @PutMapping("/deleteItemFromCart/items/{cartItemId}")
    @Operation(
            summary = "Deletes a cart-item from the cart of currently logged-in user"
    )
    public ResponseEntity<CartDto> deleteFromCart(@PathVariable int cartItemId)
    {
        CartDto cartDto = cartService.deleteFromCart(cartItemId);
        return new ResponseEntity<>(cartDto , HttpStatus.OK);
    }

    @DeleteMapping("/clearCart")
    @Operation(
            summary = "Empties the cart of the currently logged-in user.",
            description = "Deletes all the cartItems int the cart."
    )
    public ResponseEntity<ApiResponseMessage> clearMyCart()
    {
        cartService.clearCart();
        ApiResponseMessage message = ApiResponseMessage.builder().
                message("Cart has been completely emptied")
                .success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(message , HttpStatus.OK);
    }

    @GetMapping("/getMyCart")
    @Operation(
            summary = "Only For currently logged-in user. Get this user's cart.",
            description = "One user can only have one cart at a time."
    )
    public ResponseEntity<CartDto> getMyCart()
    {
        CartDto cartDto = cartService.getCart();

        return new ResponseEntity<>(cartDto , HttpStatus.OK);
    }

}
