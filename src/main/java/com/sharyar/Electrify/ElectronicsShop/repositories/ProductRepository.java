package com.sharyar.Electrify.ElectronicsShop.repositories;

import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findByNameContaining(String name , Pageable pageable);
    Page<Product> findByPriceLessThanEqual(double price ,Pageable pageable);
    Page<Product> findByBrandContaining(String brand ,Pageable pageable);
    Page<Product> findByDiscountedPriceIsNotNull(Pageable pageable);
    Page<Product> findByCategory(Category category , Pageable pageable );
    Page<Product> findByCategoryAndInStockTrue(Category category , Pageable pageable );
    Page<Product> findByCategoryAndBrand(Category category ,String brand ,Pageable pageable );

//    @Query(nativeQuery = true, value = "SELECT * FROM products WHERE category = ?, ")

}
