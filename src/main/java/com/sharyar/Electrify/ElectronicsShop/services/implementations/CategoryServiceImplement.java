package com.sharyar.Electrify.ElectronicsShop.services.implementations;

import com.sharyar.Electrify.ElectronicsShop.dto.CategoryRequestDto;
import com.sharyar.Electrify.ElectronicsShop.dto.CategoryResponseDto;
import com.sharyar.Electrify.ElectronicsShop.dto.PageableResponse;
import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import com.sharyar.Electrify.ElectronicsShop.exceptions.DuplicateValueException;
import com.sharyar.Electrify.ElectronicsShop.exceptions.ResourceNotFoundException;
import com.sharyar.Electrify.ElectronicsShop.helpers.Helper;
import com.sharyar.Electrify.ElectronicsShop.repositories.CategoryRepository;
import com.sharyar.Electrify.ElectronicsShop.services.CategoryService;
import com.sharyar.Electrify.ElectronicsShop.services.FileService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImplement implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private ModelMapper modelMapper;
    @Value("${category.profile.image.path}")
    private String categoryImagePath;
    Logger logger = LoggerFactory.getLogger(CategoryServiceImplement.class);

    //create
    public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto)
    {
        Category categoryEntity = categoryRepository.findById(categoryRequestDto.getTitle())
                .orElse(null);
        if(categoryEntity != null)
        {
            throw new DuplicateValueException("This category already exists" , HttpStatus.BAD_REQUEST);
        }
        CategoryResponseDto updatedCategoryResponseDto = null;
        try{
            categoryEntity = new Category();
            categoryEntity.setTitle(categoryRequestDto.getTitle());
            categoryEntity.setDescription(categoryRequestDto.getDescription());
            logger.info("category Dto's title is  {}" , categoryRequestDto.getTitle());
            logger.info("category Dto's description is  {}" , categoryRequestDto.getDescription());
            logger.info("category Dto's subcategories is  {}" , categoryRequestDto.getSubCategories());
            logger.info("category Dto parent is  {}" , categoryRequestDto.getParentCategory());
            logger.info("category Entity is still {}" , categoryEntity.getTitle());
            logger.info("category Entity is still {}" , categoryEntity.getDescription());
            Category parentCategory = checkCategoryExists(categoryRequestDto.getParentCategory());
            if(parentCategory != null)
            {
                categoryEntity.setParentCategory(parentCategory);
                logger.info("category Entity's parent category is {}"
                        ,categoryEntity.getParentCategory().getTitle());
                List<Category> subCategories = new LinkedList<>();
                subCategories.add(categoryEntity);
                parentCategory.setSubCategories(subCategories);
            }

            if( categoryRequestDto.getSubCategories() != null)
            {
                updatedCategoryResponseDto =  this.addSubCategories(new HashSet<>(categoryRequestDto.getSubCategories())
                        , categoryEntity);
                logger.info("categoryDto subcategories size is {}" ,
                        updatedCategoryResponseDto.getSubCategories().size());
                return updatedCategoryResponseDto;
            }
        }
        catch (Exception e)
        {
            logger.info("ERROR occured in modelmapper");
            e.printStackTrace();
        }
        Category savedCategory = categoryRepository.save(categoryEntity);
        return entityToDto(savedCategory);
    }

    //update
    public CategoryResponseDto updateCategory(CategoryRequestDto categoryRequestDto
            , String categoryName)
    {
        Category category = checkCategoryExists(categoryName);
        category.setTitle(categoryRequestDto.getTitle());
        category.setDescription(categoryRequestDto.getDescription());
        Category parentCategory = checkCategoryExists(categoryRequestDto.getParentCategory());
        category.setParentCategory(parentCategory);
        addSubCategories(Set.of(category.getTitle()) , parentCategory);
        List<String> subCategories = categoryRequestDto.getSubCategories();
        if(  subCategories != null && !subCategories.isEmpty())
        {
            addSubCategories(new HashSet<>(categoryRequestDto.getSubCategories()) , category);
        }
       Category updatedCategory = categoryRepository.save(category);

        return entityToDto(category);
    }
    public void updateCategory(Category category)
    {
            categoryRepository.save(category);
    }


    // add a SubCategory to an Existing Category
    @Override
    public CategoryResponseDto addSubCategories(Set<String> subCategoryNamesList , Category parentCategory)
    {
       Category c1 =  parentCategory;
       if(!c1.getProductList().isEmpty()){
           logger.info("Cannot add to {} category because it is already a subCategory" +
                   " with products" , c1.getTitle());
           throw new ResourceNotFoundException("The category" + c1.getTitle() +
                                                "already is a subCategory with products");
       }
       List<Category> subCategoriesList=  subCategoryNamesList.stream().map(name -> {
              Category subCategory = categoryRepository.findById(name).orElse(null);
              if(subCategory == null)
              {
                  throw new ResourceNotFoundException("Can't add subCategory : "+ name +
                        " to " + parentCategory.getTitle() + " because no subCategory " +
                          "exists by this name");
              }
              subCategory.setParentCategory(c1);
              categoryRepository.save(subCategory);
              return subCategory;
       }).toList();

           c1.setSubCategories(subCategoriesList);
           Category savedCategory = categoryRepository.save(c1);
           CategoryResponseDto categoryResponseDto = entityToDto(savedCategory);
           categoryResponseDto.setSubCategories(subCategoryNamesList);

           return categoryResponseDto;
    }

    @Override
    public String uploadCategoryImage(MultipartFile file, String categoryTitle)
    {
        Category category= checkCategoryExists(categoryTitle);
        if(category == null)
        {
            throw new ResourceNotFoundException("No category exists by this name");
        }
        if(category.getCoverImagePath() != null)
        {
            this.deleteImageFromServer(category);
        }
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);
        String pathName = categoryImagePath + category.getTitle() + "\\";

        try {
            List<String> paths = fileService.uploadFile(pathName , files );
            category.setCoverImagePath(paths.get(0));
            updateCategory(category);

            return paths.get(0);
        }
        catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public CategoryResponseDto getCategory(String categoryName) {
         Category category =  categoryRepository.findById(categoryName).orElse(null);
         if(category == null)
         {
             throw  new ResourceNotFoundException("Category named " +
                                                   categoryName + " doesnt exist!");
         }

         return entityToDto(category);
    }

    @Override
    public PageableResponse<CategoryResponseDto> getAllCategories(int pageNumber , int pageSize ,
                                                                  String sortBy , String sortDirection) {

        Sort sort = sortBy.equalsIgnoreCase("DESC") ?
                     Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        PageableResponse<CategoryResponseDto> pageableResponse =
                Helper.getPageableResponse(categoriesPage , CategoryResponseDto.class);

        return pageableResponse;
    }

    //SEARCH BY KEYWORDS
    public PageableResponse<CategoryResponseDto> getCategoryByTitleContaining(String keyword)
    {
        Sort sort = Sort.by("title").descending();
        Pageable pageable = PageRequest.of(0,3,sort);
        Page<Category> page = categoryRepository.findByTitleContainingOrderByTitleAsc(keyword , pageable);

      return  Helper.getPageableResponse(page , CategoryResponseDto.class);
    }

    @Override
    public void deleteCategory(String categoryName) {

       Category category = categoryRepository.findById(categoryName).orElse(null);
       if ( category != null)
       {
           this.deleteCategory(category);
       }
       else {
           throw new ResourceNotFoundException("No category exists by name : " + categoryName);
       }
    }

    private void deleteCategory(Category category)
    {
        boolean deleted = this.deleteImageFromServer(category);
        if(deleted)
        {
            logger.info("Image file deleted from the server.");
        }
        else
        {
            throw new RuntimeException("Can't category delete image file from server");
        }
        Category parentCategory = category.getParentCategory();
        List<Category> childCategories =  category.getSubCategories();
        if(parentCategory != null)
        {
            category.removeParentCategory();
            parentCategory.removeASubCategory(category);
        }
        if (!childCategories.isEmpty())
        {
            //recursion
            Category[] childCategoriesArray = new Category[childCategories.size()];
            childCategories.toArray(childCategoriesArray);
            int size = childCategoriesArray.length;
            for(int i=0 ; i<size ; i++ )
            {
                this.deleteCategory(childCategoriesArray[i]);
            }
        }
        else
        {
            Set<Product> productList = category.getProductList();
            if( !productList.isEmpty())
            {
                Iterator<Product> it = productList.iterator();
                while(it.hasNext())
                {    try
                     {
                       fileService.deleteFilesFromServer(it.next());
                       it.remove();
                     }
                     catch (IOException e)
                     {
                         throw new RuntimeException("One of the product's files" +
                                 "CANNOT BE DELETED from server");
                     }
                }
//                category.removeAllProducts();
            }
        }
        categoryRepository.delete(category);
    }

    private boolean deleteImageFromServer(Category category)
    {
        String pathString = category.getCoverImagePath();
        if(pathString != null)
        {
            Path imagePath = Path.of(pathString);
            try {
                if(Files.deleteIfExists(imagePath))
                {
                    return  true;
                }

            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // NOW DELETING THE EMPTY FOLDER:
        Path emptyFolderPath = Path.of(categoryImagePath + category.getTitle());
        try{
            Files.delete(emptyFolderPath);
        }
        catch (java.io.IOException e)
        {
            logger.info("IOException during deleting product files from server");
            throw new RuntimeException(e.getMessage());
        }
        return  true;

    }

    public Category checkCategoryExists(String categoryId)
    {
        if( categoryId == null)
        {
            return null;
        }
         Category category = categoryRepository.findById(categoryId).orElse(null);
         if(category == null)
         {
             throw new ResourceNotFoundException("No category exists by this Id " + categoryId);
         }
         return category;
    }

//    public Category dtoToEntity(CategoryDto dto)
//    {
////           Category category = new Category();
//           category.setTitle(dto.getTitle());
//           category.setDescription(dto.getDescription());
//           if(dto.getCoverImage() != null)
//           {
//               category.setCoverImage(dto.getCoverImage());
//           }
//           Category parentCategory = checkCategoryExists(dto.getParentCategory());
//           category.setParentCategory(parentCategory);
//           if(dto.getSubCategories() != null)
//           {
//               addSubCategories(new HashSet<>(dto.getSubCategories() ) , category );
//           }
//
//           return category;
//    }

    public CategoryResponseDto entityToDto(Category category)
    {
           CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
           categoryResponseDto.setTitle(category.getTitle());
           categoryResponseDto.setDescription(category.getDescription());
           categoryResponseDto.setCoverImagePath(category.getCoverImagePath());
           int totalProductsInCategory = category.getProductList().size();
           logger.info("this category  has {} products" , totalProductsInCategory );
           categoryResponseDto.setTotalProducts(totalProductsInCategory);
           Category parentCategory = category.getParentCategory();
           if(parentCategory != null)
           {
            categoryResponseDto.setParentCategory(parentCategory.getTitle());
           }
           if(category.getSubCategories() != null)
           {
            Set<String> subCatStrings = category.getSubCategories().stream().map(
                    Category::getTitle).collect(Collectors.toSet());
            categoryResponseDto.setSubCategories( subCatStrings);
           }
        return categoryResponseDto;
    }


}
