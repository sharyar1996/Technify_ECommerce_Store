package com.sharyar.Electrify.ElectronicsShop.services;

import com.sharyar.Electrify.ElectronicsShop.dto.CategoryRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.CategoryResponseDto;
import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface CategoryService {

      //create
      CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);

      //update
      CategoryResponseDto updateCategory(CategoryRequestDto categoryRequestDto, String categoryName);
      public void updateCategory(Category category);

      //Add subcategories to an Existing category:
      public CategoryResponseDto addSubCategories(Set<String> subCategoriesDto ,
                                                  Category parentCategory);

      String uploadCategoryImage(MultipartFile file, String categoryTitle);

      //get Single Category
      CategoryResponseDto getCategory(String categoryName);
      //get All Categories
      PageableResponse<CategoryResponseDto> getAllCategories(int pageNumber , int pageSize,
                                                             String sortBy, String sortDirection);
      PageableResponse<CategoryResponseDto> getCategoryByTitleContaining(String keyword);
      Category checkCategoryExists(String categoryId);
      //delete Category
      void deleteCategory(String categoryName);

}
