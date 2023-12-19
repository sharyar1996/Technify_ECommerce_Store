package com.sharyar.Electrify.ElectronicsShop.dto;

import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class ProductResponseDto {

    private String productId;
    @NotBlank
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
    private LocalDate addedDate;
    //Here warranty is string because we want to send to the user
    // in a proper readable format.
    private String warranty;
    private boolean inStock =true;
    private double avgRating;
    private int timesThisProductHasBeenRated;
    private Map<UserResponseDto, Double> userRatings = new HashMap<>();
    private String category;
    private List<String> productImages;

    public ProductResponseDto() {

    }

    public ProductResponseDto(String productId, String name, String brand, String modelNumber, String description, double price, double discountedPrice, int quantityAvailable, LocalDate addedDate, String warranty, boolean inStock, double avgRating, List<String> productImages) {
        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.modelNumber = modelNumber;
        this.description = description;
        this.price = price;
        this.discountedPrice = discountedPrice;
        this.quantityAvailable = quantityAvailable;
        this.addedDate = addedDate;
        this.warranty = warranty;
        this.inStock = inStock;
        this.avgRating = avgRating;
        this.productImages = productImages;
//        this.parentCategory = parentCategory;
        System.out.println("constructor method ran of productDto");
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public LocalDate getAddedDate() {
        return this.addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        if( warranty == 0)
        {
            this.warranty = "No warranty";
        }
        int yearsLeft = warranty/12;
        int monthsLeft = warranty%12;

        this.warranty = yearsLeft + " years ," + monthsLeft + " months";
    }

    public void setCategory(Category category) {
        System.out.println("setCategory method ran in productDto with Category as param");
        if(category != null)
        {
           this.category = category.getTitle();
        }
    }

    public void setCategory(String categoryName) {
        System.out.println("setCategory method ran in productDto with Category as param");
        this.category = categoryName;
    }




}
