package com.shashank.electronic.store.repositories;

import com.shashank.electronic.store.models.Cart;
import com.shashank.electronic.store.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, String> {

    Optional<Cart> findByUser(User user);
}
