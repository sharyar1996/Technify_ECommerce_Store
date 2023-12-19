package com.sharyar.Electrify.ElectronicsShop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private int CartItemId;
    private ProductResponseDto product;
    private int count = 1;
    private double itemBill;
}
