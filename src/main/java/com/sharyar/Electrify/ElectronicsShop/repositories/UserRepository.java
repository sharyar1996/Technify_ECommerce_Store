package com.sharyar.Electrify.ElectronicsShop.repositories;

import com.sharyar.Electrify.ElectronicsShop.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    //Custom Finder methods
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndPassword(String email, String password);

    //for Search method in service
    List<User> findByUserNameContaining(String keyword);
}
