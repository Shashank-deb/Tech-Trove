package com.shashank.electronic.store.services;

import com.shashank.electronic.store.dtos.CreateOrderRequest;
import com.shashank.electronic.store.dtos.OrderDTO;
import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.models.Order;

import java.util.List;

public interface OrderService {

    // Create Order
    public OrderDTO createOrder(CreateOrderRequest orderDTO);

    //Remove Order
    void removeOrder(String orderId);

    //Get Order of user
    List<OrderDTO> getOrdersOfUser(String userId);

    // Get all Order
    PageableResponse<OrderDTO> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir);


    //updating the order
    public OrderDTO updateOrder(String orderId, OrderDTO orderDTO);


}
