package com.sharyar.Electrify.ElectronicsShop.controllers;

import com.sharyar.Electrify.ElectronicsShop.dto.ApiResponseMessage;
import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.dto.ProductRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.ProductResponseDto;
import com.sharyar.Electrify.ElectronicsShop.exceptions.BadRequestApiException;
import com.sharyar.Electrify.ElectronicsShop.helpers.Helper;
import com.sharyar.Electrify.ElectronicsShop.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
public class ProductController {

    @Autowired
    CategoryController categoryController;
    @Autowired
    ProductService productService;

    //addProduct
    @PostMapping("/createProduct")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a new product. (ONLY FOR ADMINS)",
            description = "You can either set the category right here by sending the" +
                    " category name(if that category already exists , otherwise " +
                    " a Runtime exception will be thrown) or you can set the category to this" +
                    " product by calling the separate API endpoint " +
                    "'/products/addProductToCategory' .\n" +
                    "NOTE: A category can only have products if that category " +
                    "doesn't have any sub-categories.\n" +
                    "NOTE: warranty should be entered as number of months."
    )
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid ProductRequestDto productRequestDto) throws IOException {

        ProductResponseDto dto = productService.createProduct(productRequestDto);

        return  new ResponseEntity<>(dto , HttpStatus.CREATED);
    }

    //uploadImages
    @PostMapping("/uploadImages/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add images to an existing product. (ONLY FOR ADMIN)",
            description = "Allowed image format/types : \n"  +
                    "'image/jpg' , 'image/jpeg' , 'image/png' , 'image/webp' , 'image/gif' ."
    )
    public ResponseEntity<ProductResponseDto> uploadImages(@Size(max = 4 ,
    message = "A maximum of 4 images allowed per Product") @RequestBody MultipartFile[] files
                                                 , @PathVariable String productId)
            throws IOException {
        List<MultipartFile> files2 = Arrays.asList(files);
        if(!Helper.validateMultipartFiles(files2))
        {
            throw new BadRequestApiException("One or more of the images have" +
                                        " Invalid format/type");
        }
       ProductResponseDto savedProductResponseDto = productService.uploadProductImagesToServer(productId,files2);
       return new ResponseEntity<>(savedProductResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a new product. (ONLY FOR ADMIN)",
            description = "Takes in productId of an existing product , ProductReqquestDto as " +
                    " the new product's details as parameter. Returns the updated product " +
                    "i.e ProductResponseDto."
    )
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable  String id ,
            @RequestBody() ProductRequestDto productRequestDto)
    {
        ProductResponseDto productResponseDtoUpdated = productService.updateProduct(id , productRequestDto);
        return new ResponseEntity<>(productResponseDtoUpdated, HttpStatus.OK);
    }

    @GetMapping("/searchByName/{name}")
    public ResponseEntity<PageableResponse<ProductResponseDto>> searchByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
           PageableResponse<ProductResponseDto> response = productService.searchByName(name, pageNumber, pageSize, sortBy , sortDir);

//           return  response;
          return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @PutMapping("/addProductToCategory")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            description = "Takes in a productId , categoryTitle as " +
                    "parameters.A product can only have one category " +
                    "and a  product can be added to only that category which " +
                    "does not have any sub-categories.",
            summary = "Add a product to an existing category. ONLY FOR ADMIN.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Either the category or product doesn't exist",
                            responseCode = "404"
                    )
            }
    )
    public ResponseEntity<ApiResponseMessage> addToCategory(
            @RequestParam String productId ,
            @RequestParam String categoryTitle)
    {
         productService.addProductToCategory(productId,  categoryTitle);
         ApiResponseMessage response = ApiResponseMessage.builder().message("Product "
                         + productId + " has been added to "
                    + " category : " + categoryTitle)
                 .success(true)
                 .status(HttpStatus.OK)
                 .build();
         return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ProductResponseDto> getById(@PathVariable() String id)
    {
         ProductResponseDto productResponseDto = productService.getProductById(id);
         return new ResponseEntity<>(productResponseDto, HttpStatus.OK);
    }

    @GetMapping("/getProductsByCategoryInStock/{categoryTitle}")
    public ResponseEntity<PageableResponse<ProductResponseDto>> getByCategoryInStockProducts(
            @PathVariable String categoryTitle,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "category") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
         PageableResponse<ProductResponseDto> response =
                 productService.searchByCategoryInStockProducts(categoryTitle, pageNumber
                , pageSize, sortBy ,sortDir);
         return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @GetMapping("/searchByBrand/{brandName}")
    public ResponseEntity<PageableResponse<ProductResponseDto>> getProductsByBrand(
            @PathVariable String brandName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "brand") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
         PageableResponse<ProductResponseDto> response =productService.searchByBrand(brandName, pageNumber
                , pageSize, sortBy ,sortDir);
         return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @GetMapping("/getAll")
    public ResponseEntity<PageableResponse<ProductResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
        PageableResponse<ProductResponseDto> response =productService.getAllProducts(pageNumber
                , pageSize, sortBy ,sortDir);
        return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @GetMapping("/searchByCategory/{categoryName}")
    public ResponseEntity<PageableResponse<ProductResponseDto>> searchByCategory(
            @PathVariable() String categoryName,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "category") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
        PageableResponse<ProductResponseDto> response =productService.searchByCategory(
                categoryName , pageNumber, pageSize, sortBy ,sortDir);
        return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @GetMapping("/searchDiscountedProducts")
    public ResponseEntity<PageableResponse<ProductResponseDto>> getDiscountedProducts(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "discountedPrice") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
        PageableResponse<ProductResponseDto> response =productService.searchByProductsOnDiscount(
                pageNumber, pageSize, sortBy ,sortDir);
        return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @GetMapping("/searchProductsUnderPrice/{price}")
    public ResponseEntity<PageableResponse<ProductResponseDto>> searchProductsUnderPrice(
            @PathVariable() double price,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
        PageableResponse<ProductResponseDto> response =productService.searchByMaxPrice( price,
                pageNumber, pageSize, sortDir);
        return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @GetMapping("/searchByBrandUnderCategory")
    public ResponseEntity<PageableResponse<ProductResponseDto>> searchProductsUnderPrice(
            @RequestParam() String categoryName,
            @RequestParam() String brand,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir)
    {
        PageableResponse<ProductResponseDto> response =productService.searchByCategoryAndProductBrand(
                categoryName,brand,pageNumber, pageSize,sortBy ,sortDir);
        return  new ResponseEntity<>(response , HttpStatus.OK );
    }

    @Operation(
         summary = "Rate a product(value should be between 1-10)",
         description = "A user can only rate a product if that product is found to be in " +
                 " user's order history meaning that the user " +
                 "has bought the product from our website int the " +
                 "past otherwise a BadRequestException will be thrown."
    )
    @PutMapping("/rateProduct")
    public ResponseEntity<ApiResponseMessage> rateProduct(
            @RequestParam String productId,
            @RequestParam double productRating
    )
    {
        if( productRating < 1)
        {
            throw new BadRequestApiException("Invalid productRating.");
        }
             productService.rateProduct(productId, productRating);
             ApiResponseMessage responseMessage =
                     ApiResponseMessage.builder().message("Product ratings updated.")
                     .status(HttpStatus.OK).success(true).build();
             return  new ResponseEntity<>(responseMessage ,HttpStatus.OK);
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a product. ONLY FOR ADMIN",
            description = "Takes in productId as a parameter. " +
                    "By deleting a product, all its images will also be deleted from " +
                    "the server. Also, the product will be removed from its category." +
                    "Also , any previous orderItem containing this product will have " +
                    "will no longer have the product , but that orderItem will still " +
                    "remain in the user's order history."
    )
    public ResponseEntity<ApiResponseMessage> deleteProduct( @PathVariable String productId)
    {
        productService.deleteProduct(productId);
        ApiResponseMessage response = ApiResponseMessage.builder().message("Product "
                        + productId + " has been deleted")
                .status(HttpStatus.OK).success(true).build();
        return  new ResponseEntity<>(response , HttpStatus.OK);
    }


}
