package com.sharyar.Electrify.ElectronicsShop.dto;

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
public class CartDto {
    private UserResponseDto userResponseDto;
    private Set<CartItemDto> cartItemSet = new LinkedHashSet<>();
    private LocalDate createdOn;
    private double totalBill;

}
