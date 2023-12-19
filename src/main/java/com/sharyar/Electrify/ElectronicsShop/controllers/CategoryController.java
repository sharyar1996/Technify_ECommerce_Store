package com.sharyar.Electrify.ElectronicsShop.controllers;

import com.sharyar.Electrify.ElectronicsShop.dto.*;
import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.services.CategoryService;
import com.sharyar.Electrify.ElectronicsShop.services.FileService;
import com.sharyar.Electrify.ElectronicsShop.validations.imageValidation.ImageNameValid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/categories")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${category.profile.image.path}")
    String categoryImagePath;

    Logger logger = LoggerFactory.getLogger(CategoryController.class);


    //Create category:
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add a new category (ONLY FOR ADMINS)",
            description = "A category can only have either of sub-categories or products" +
                    " directly.No category can have direct products as well " +
                    " as products.Doing that will lead to a Bad Request exception" +
                    ".You can either set the sub-categories right here by sending the" +
                    " sub-categories list(if those sub-categories already exist , otherwise " +
                    " a Runtime exception will be thrown) or you can set the sub-categories " +
                    "to any parent category by calling the separate API endpoint" +
                    " '/categories/addSubCategories'.",
            responses = {
                    @ApiResponse(
                            responseCode = "403",
                            description = "Only an admin can access this endpoint."
                    )
            }
    )
    public ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody @Valid CategoryRequestDto categoryRequestDto)
    {
        logger.info("In controller before service call : {}" , categoryRequestDto.getTitle());
        CategoryResponseDto savedCategory = categoryService.createCategory(categoryRequestDto);
        logger.info("In controller after service call");
         return new ResponseEntity<>(modelMapper.map(savedCategory, CategoryResponseDto.class),
                 HttpStatus.CREATED);
    }

    //update category:
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{categoryName}")
    @Operation(
            summary = "Update an existing category.(ONLY FOR ADMINS)"
    )
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @RequestBody @Valid CategoryRequestDto categoryRequestDto
            , @PathVariable("categoryName") String name )
    {
        CategoryResponseDto updatedCategoryResponseDto = categoryService
                .updateCategory(categoryRequestDto, name);

        return new ResponseEntity<>(updatedCategoryResponseDto, HttpStatus.OK);
    }

    // Add subCategories to an Existing Category:
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update/addSubCategories/{parentCategoryName}")
    @Operation(
            summary = "Add sub-categories to an existing category.(ONLY FOR ADMINS)",
            description = "NOTE: A category can only have sub-categories if that category " +
                    " directly doesn't have any products. So make sure the " +
                    " parent category doesnt have any direct products" +
                    " if you want to add sub-categories to it."
    )
    public ResponseEntity<CategoryResponseDto> addSubCategories(
            @RequestBody List<String> subCategoriesName,
            @PathVariable("parentCategoryName") String parentCategoryName)
    {
             Category parentCategory = categoryService.checkCategoryExists(parentCategoryName);
             logger.info("SubCategories to add = {}" , subCategoriesName );
             Set<String> set =new HashSet<>(subCategoriesName);
             CategoryResponseDto updatedParentCategoryResponseDto =  categoryService.addSubCategories(set ,
                                                     parentCategory);
             logger.info("In Controller :After service implementation of addSubCategories.");

         return new ResponseEntity<>(updatedParentCategoryResponseDto, HttpStatus.CREATED);
    }

   //GET BY ID
    @GetMapping("/getCategory/{categoryName}")
    @Operation(
            summary = "Get All categories along with the number of " +
                    " products in each category",
            description = "If you want to search products by each category , " +
                    "use '/products/searchByCategory' instead of this endpoint. "
    )
    public ResponseEntity<CategoryResponseDto> getSingleCategory(@PathVariable String categoryName)
    {
              CategoryResponseDto categoryResponseDto = categoryService.getCategory(categoryName);
              return new ResponseEntity<>(categoryResponseDto, HttpStatus.OK);
    }

    private Category checkCategoryExists(String categoryId)
    {
        Category category= categoryService.checkCategoryExists(categoryId);
        logger.info("category = {}" , category.getTitle());
        return category;
    }

    //GET ALL
    @GetMapping("/getAllCategories")
    @Operation(
            summary = "Get All categories along with the number of " +
                    " products in each category.",
            description = "If you want to search products by each category , " +
                    "use '/products/searchByCategory' instead of this endpoint. "
    )
    public ResponseEntity<PageableResponse<CategoryResponseDto>> getAllCategories(
            @RequestParam(value="pageNumber" , required = false , defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize" , required = false ,defaultValue = "5") int pageSize,
            @RequestParam(value = "sortBy" , required = false , defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir" , required = false , defaultValue = "ASC") String sortDir
    )
    {
         PageableResponse<CategoryResponseDto> response = categoryService.getAllCategories(pageNumber,pageSize,sortBy , sortDir);

        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    //SEARCH BY KEYWORD
    @GetMapping("/searchByKeyword")
    @Operation(
            summary = "Search a  category by any keyword ",
            description = "If you want to search products by each categories , " +
                    "use '/products/searchByCategory' instead of this endpoint. "
    )
    public ResponseEntity<PageableResponse<CategoryResponseDto>> searchCategoriesByKeyword(
            @RequestParam("keyword") String keyword)
    {
        PageableResponse<CategoryResponseDto> response= categoryService.getCategoryByTitleContaining(keyword);
        return  new ResponseEntity<>(response , HttpStatus.OK);
    }

    // DELETE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{categoryTitle}")
    @Operation(
            summary = "ONLY FOR ADMINS. Delete category by its name.",
            description = "Takes in the categoryTitle as a parameter. " +
                    "Once this API is called , all the sub-categories (including their " +
                    "products) of this deleted category will also be deleted and this" +
                    "category will be removed from its parent category's list( if it has" +
                    " any parent category)."
    )
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryTitle)
    {
        categoryService.deleteCategory(categoryTitle);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder().
                message("Category : " + categoryTitle + " has been successfully deleted")
                .success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(responseMessage , responseMessage.getStatus());
    }

    //UPLOAD CATEGORY PHOTO
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/uploadPhoto/{categoryTitle}")
    @Operation(
            summary = "Add images to an existing category. (ONLY FOR ADMINS)",
            description = "Takes in a MultipartFile as a parameter . " +
                    "Returns an ImageResponse with details such as the file path on the" +
                    "server on which the the image is saved." +
                    " Allowed image format/types : \n"  +
                    "'image/jpg' , 'image/jpeg' , 'image/png' , 'image/webp' , 'image/gif' ."
    )
    public ResponseEntity<ImageResponse> uploadPhoto( @ImageNameValid @RequestBody MultipartFile file ,
                                                     @PathVariable String categoryTitle)
    {
        String imagePathOnServer = categoryService.uploadCategoryImage(file , categoryTitle);
        ImageResponse response = ImageResponse.builder().imageName(imagePathOnServer)
                .success(true).status(HttpStatus.CREATED)
                .message("Your image has been uploaded")
                .build();

        return new ResponseEntity<>(response, response.getStatus());
    }

    //SERVE CATEGORY PHOTO
    @GetMapping("/servePhoto/{categoryTitle}")
    public void serveCategoryPhoto(@PathVariable String categoryTitle ,
                                   HttpServletResponse response)
    {
        response.setContentType("image/jpeg");
        CategoryResponseDto categoryResponseDto = categoryService.getCategory(categoryTitle);
        InputStream inputStream = fileService.getResource(categoryImagePath
                , categoryResponseDto.getCoverImagePath() );
        try {
            StreamUtils.copy(inputStream , response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
