package com.sharyar.Electrify.ElectronicsShop.services;


import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.ProductRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.ProductResponseDto;
import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto productRequestDto) throws IOException;

    ProductResponseDto uploadProductImagesToServer(String productId ,
                                                          List<MultipartFile> multipartFiles) throws IOException;
    void rateProduct(String productId , double productRating);
    void addProductToCategory(String productId , String categoryId);
    ProductResponseDto getProductById(String productId);
    PageableResponse<ProductResponseDto> getAllProducts(int pageNumber , int pageSize,
                                                        String sortBy , String sortDir);
    PageableResponse<ProductResponseDto> searchByName(String name , int pageNumber
                                            , int pageSize , String sortBy , String sortDir);
    public PageableResponse<ProductResponseDto> searchByBrand(String brandName, int pageNumber,
                                                              int pageSize, String sortBy
            , String sortDir);
    PageableResponse<ProductResponseDto> searchByMaxPrice(double price , int pageNumber
            , int pageSize , String sortDir);

    PageableResponse<ProductResponseDto> searchByCategoryAndProductBrand(String categoryName , String brand
            , int pageNumber, int pageSize, String sortBy , String sortDir);
    public PageableResponse<ProductResponseDto> searchByCategory(String categoryName, int pageNumber,
                                                                 int pageSize, String sortBy , String sortDir);
    public PageableResponse<ProductResponseDto> searchByCategoryInStockProducts(String categoryName, int pageNumber,
                                                                                int pageSize, String sortBy
                                                                        , String sortDir);
    public PageableResponse<ProductResponseDto> searchByProductsOnDiscount(int pageNumber,
                                                                           int pageSize, String sortBy
                                                                  , String sortDir);
    ProductResponseDto updateProduct(String productId, ProductRequestDto productRequestDto);
    public void updateProduct(Product product);
    void deleteProduct(String productId);
    Product checkProductExists(String id);

    void deleteProduct(Product product);
}
