package com.sharyar.Electrify.ElectronicsShop.dto;

import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {

    private String title;
    private String description;
    private String coverImagePath;
    private boolean hasSubCategories;
    private int totalProducts;
    private String parentCategory;
    private List<String> subCategories;

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }
    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory.getTitle();
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Set<String> subCategories) {
        if(subCategories == null)
        {
            return;
        }
        this.subCategories = new ArrayList<>(subCategories);
    }
//    public void setSubCategories(List<Category> subCategories) {
//        if(subCategories == null)
//        {
//            return;
//        }
//      this.subCategories = subCategories.stream().map( subCategory -> {
//               return subCategory.getTitle();
//         }).collect(Collectors.toList());
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        if(coverImagePath == null)
        {
            return;
        }
        this.coverImagePath = coverImagePath;
    }

    public boolean getHasSubCategories() {
        return hasSubCategories;
    }

    public void setHasSubCategories(boolean hasSubCategories) {
        this.hasSubCategories = hasSubCategories;
    }


    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

}
