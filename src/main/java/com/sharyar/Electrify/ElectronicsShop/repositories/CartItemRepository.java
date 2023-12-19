package com.sharyar.Electrify.ElectronicsShop.repositories;

import com.sharyar.Electrify.ElectronicsShop.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
