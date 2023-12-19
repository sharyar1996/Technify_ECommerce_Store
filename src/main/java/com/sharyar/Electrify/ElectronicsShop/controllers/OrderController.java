package com.sharyar.Electrify.ElectronicsShop.controllers;

import com.sharyar.Electrify.ElectronicsShop.dto.ApiResponseMessage;
import com.sharyar.Electrify.ElectronicsShop.dto.BillingAddressDto;
import com.sharyar.Electrify.ElectronicsShop.dto.OrderDto;
import com.sharyar.Electrify.ElectronicsShop.dto.UserResponseDto;
import com.sharyar.Electrify.ElectronicsShop.services.BillingAddressService;
import com.sharyar.Electrify.ElectronicsShop.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private BillingAddressService billingAddressService;


    @PostMapping("/setMyBillingAddress")
    @Operation(
            summary = "Sets the Billing Address details of the currently logged-in" +
                    " users.",
            description = "One user can have only one Billing Address.\n " +
                    "We only have shipment available for only these cities: \n" +
                    "  'karachi' , 'lahore' , 'faisalabad' ,'rawalpindi',\n" +
                    " 'quetta', 'peshawar' , 'multan' , 'hyderabad'. \n" +
                    "The phone number should skip country code as it is already " +
                    " taken care of."
    )
    public ResponseEntity<UserResponseDto> setBillingAddress(@RequestBody
                                                                 @Valid BillingAddressDto dto)
    {
       UserResponseDto savedUserResponseDto = billingAddressService.setBillingAddress(dto);
       return  new ResponseEntity<>(savedUserResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/getMyBillingAddress")
    @Operation(
            summary = "Gets the Billing Address details of the currently logged-in" +
                    " users."
    )
    public ResponseEntity<BillingAddressDto> getBillingAddress()
    {
       BillingAddressDto billingResponseDto = billingAddressService.getBillingAddress();
       return  new ResponseEntity<>(billingResponseDto,HttpStatus.OK);
    }

    @PostMapping("/createOrder")
    @Operation(
            summary = "Creates the order of the currently logged-in user",
            description = "The order will only be created if the user's cart " +
                    " is not empty else a Runtime exception will be thrown. " +
                    "Also , if the user has any previously created and confirmed" +
                    " orders pending(means the payment is still due) , then too " +
                    "this new order will not be created." +
                    "NOTE: If the billing address of the user is null, a " +
                    "BadRequestException will be thrown."
    )
    public ResponseEntity<OrderDto> createOrder()
    {
        OrderDto orderDto = orderService.proceedToCheckout();
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    @PutMapping("/confirmAndPlaceOrder")
    @Operation(
            summary = "Places the order of the currently logged-in user and dispatches " +
                    "it to the user's billing address.",
            description = "If the user's order history doesn't have any " +
                    " new created order , then it will throw a BadRequestException."
    )
    public ResponseEntity<ApiResponseMessage> placeOrder()
    {
        orderService.confirmAndShipToAddress();

        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("Your order has been dispatched to be shipped to your address")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message , HttpStatus.OK);
    }

    @GetMapping("/userOrderHistory/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Gets the order history of any user. (ONLY FOR ADMINS)"
    )
    public ResponseEntity<List<OrderDto>> getAllOrdersByAUser(@PathVariable String userId)
    {
        List<OrderDto> list =  orderService.getAllOrdersByAUser(userId);
        return new ResponseEntity<>(list , HttpStatus.OK);
    }

    @GetMapping("/getMyOrderHistory")
    @Operation(
            summary = "Gets the order history of the currently logged-in user"
    )
    public ResponseEntity<List<OrderDto>> getAllMyOrders()
    {
        List<OrderDto> list =  orderService.getMyOrderHistory();
        return new ResponseEntity<>(list , HttpStatus.OK);
    }


}
