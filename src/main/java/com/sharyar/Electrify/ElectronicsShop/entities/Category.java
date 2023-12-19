package com.sharyar.Electrify.ElectronicsShop.entities;

import com.sharyar.Electrify.ElectronicsShop.exceptions.BadRequestApiException;
import com.sharyar.Electrify.ElectronicsShop.repositories.CategoryRepository;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="categories")
public class Category {

    @Id
    @Column(name = "category_title" , length = 50 , updatable = false)
    private String title;

    @Column(name="category_description")
    private String description;

    @Column(name = "category_image")
    private String coverImagePath;

    private boolean hasSubCategories = false;

    @ManyToOne(cascade =  CascadeType.PERSIST )
    private Category parentCategory ;

    @Column(name = "child_categories")
    @OneToMany()
    private List<Category> subCategories = new LinkedList<>();

    @OneToMany(cascade = CascadeType.REMOVE , orphanRemoval = true , fetch = FetchType.LAZY)
    private Set<Product> productList = new HashSet<>();


    public void setProductList (Set<Product> products)
    {
        if (hasSubCategories)
        {
            throw new BadRequestApiException(this.title + " has subCategories so we can't " +
                    "add products in this category.(Only categories without any child" +
                    "categories will have products)");
        }
        System.out.println("Adding "+ "products to category " + this.getTitle());
        this.productList.addAll(products);
    }

    public void setSubCategories( List<Category> subCategories)
    {
            if(subCategories != null && this.productList.isEmpty())
            {
                System.out.println("adding new subcategories to parent!");
                this.subCategories.addAll(subCategories);
                this.hasSubCategories = true;
            }
            else
            {
                System.out.println("This category " + title + " cannot have subCategories"
                                  + " because it already has products in it." +
                        "(No category can have subcategories if it has products)");
            }

    }

    public void setParentCategory(Category parent )
    {
            if (parent != null)
            {
                System.out.println("Adding " + this.getTitle() +
                        " 's parent as " + parent   );
                this.parentCategory = parent;
                System.out.println( this.getTitle() +".parentCategoryName = " + this.parentCategory);
            }
    }

    public void removeParentCategory()
    {
        this.parentCategory =null;
    }
    public void removeASubCategory(Category category)
    {
        this.subCategories.remove(category);
    }
    public void removeAllSubCategories()
    {
        this.subCategories.forEach(Category::removeParentCategory);
        this.subCategories = new LinkedList<>();
    }
    public void removeAProduct(Product product)
    {
        this.productList.remove(product);
    }
    public void removeAllProducts()
    {
        this.productList.clear();
    }

}


