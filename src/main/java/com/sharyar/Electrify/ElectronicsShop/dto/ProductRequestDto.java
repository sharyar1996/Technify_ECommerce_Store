package com.sharyar.Electrify.ElectronicsShop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    private String name;
    @NotBlank
    private String brand;
    @NotBlank
    private String modelNumber;
    private String description;
    @Positive(message = "Price should be greater than 0")
    private double price;
    private double discountedPrice;
    private int quantityAvailable;
    // warranty is in months
    private int warranty;
    private String category;

}
