package com.sharyar.Electrify.ElectronicsShop.repositories;

import com.sharyar.Electrify.ElectronicsShop.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category , String> {

    //Custom Finder methods:
    Page<Category> findByTitleContainingOrderByTitleAsc(String keyword , Pageable pageable);
    Page<Category> findByTitleContainingOrderByTitleDesc(String keyword , Pageable pageable);

}
