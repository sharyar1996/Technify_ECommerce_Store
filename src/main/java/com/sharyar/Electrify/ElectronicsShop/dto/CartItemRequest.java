package com.sharyar.Electrify.ElectronicsShop.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    private String productId;
    @Min(1)
    private int count;
}
