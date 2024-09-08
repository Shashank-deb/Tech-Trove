package com.shashank.electronic.store.repositories;

import com.shashank.electronic.store.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}
