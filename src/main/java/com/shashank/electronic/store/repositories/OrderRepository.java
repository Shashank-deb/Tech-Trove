package com.shashank.electronic.store.repositories;

import com.shashank.electronic.store.models.Order;
import com.shashank.electronic.store.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {


    List<Order> findByUser(User user);
}
