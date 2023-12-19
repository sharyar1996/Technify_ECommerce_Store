package com.sharyar.Electrify.ElectronicsShop.repositories;

import com.sharyar.Electrify.ElectronicsShop.entities.Cart;
import com.sharyar.Electrify.ElectronicsShop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart , String > {

    Cart findByUser(User user);
}
