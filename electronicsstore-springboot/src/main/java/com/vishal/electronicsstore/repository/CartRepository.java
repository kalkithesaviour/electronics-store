package com.vishal.electronicsstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vishal.electronicsstore.entity.Cart;
import com.vishal.electronicsstore.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUser(User user);

}
