package com.sharyar.Electrify.ElectronicsShop.repositories;

import com.sharyar.Electrify.ElectronicsShop.entities.OrderItem;
import com.sharyar.Electrify.ElectronicsShop.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface OrderItemRepository extends JpaRepository< OrderItem , Integer> {

    List<OrderItem> findByProduct(Product product);
}
