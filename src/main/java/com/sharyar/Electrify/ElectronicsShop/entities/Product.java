package com.sharyar.Electrify.ElectronicsShop.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Length;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;


@Getter
@Setter
@Entity
@Table(name= "products")
public class Product {
    @Id
    private String productId;
    @Column(length= 3000 , unique = true , nullable = false)
    private String name;
    private String brand;
    private String modelNumber;
    @Column(length = 10000)
    private String description;
    private double price;
    private double discountedPrice;
    private int quantityAvailable;
    private LocalDate addedDate;
    //warranty will be stored as months in database
    private int warranty;
    private boolean inStock ;
    private double avgRating;
    private int timesThisProductHasBeenRated;
    @OneToMany(cascade = CascadeType.ALL , orphanRemoval = true)
    private Set<ProductRating> productRatings = new HashSet<>();
    @Column(length = 16777215 )
    private List<String> productImages;
    @ManyToOne(cascade = CascadeType.PERSIST , fetch = FetchType.EAGER  )
    @JoinColumn(name = "parentCategory")
    private Category category;

    public Product() {
    }

    public Product(String productId, String name, String brand, String modelNumber,
                   String description, double price, double discountedPrice,
                   int quantityAvailable, LocalDate addedDate, int warranty,
                   boolean inStock, double avgRating, List<String> productImages
                   , Category category) {
        this.productId = productId;
        this.brand = brand;
        this.name = name;
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
        this.category = category;
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
        if(quantityAvailable > 0)
        {
            this.inStock = true;
        }
        this.quantityAvailable = quantityAvailable;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }

    public boolean getInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double newRating) {
        timesThisProductHasBeenRated++;
        this.avgRating = (this.avgRating + newRating) / timesThisProductHasBeenRated ;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if(category != null)
        {
            this.category = category;

        }
    }
    public void setCategory(String categoryId) {
        if(category != null)
        {
            this.category = category;

        }
    }
    public void removeCategory()
    {
        this.category = null;
    }




}
