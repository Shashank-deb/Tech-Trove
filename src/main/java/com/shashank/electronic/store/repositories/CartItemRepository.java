package com.shashank.electronic.store.repositories;

import com.shashank.electronic.store.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
