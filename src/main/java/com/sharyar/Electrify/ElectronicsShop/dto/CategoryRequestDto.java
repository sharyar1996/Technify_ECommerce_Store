package com.sharyar.Electrify.ElectronicsShop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {

    @NotBlank
    @Size(min = 2)
    private String title;
    @NotBlank
    private String description;
    private boolean hasSubCategories;
    private String parentCategory;
    private List<String> subCategories;
}
