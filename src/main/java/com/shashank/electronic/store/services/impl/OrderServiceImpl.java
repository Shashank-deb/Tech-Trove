package com.shashank.electronic.store.services.impl;

import com.shashank.electronic.store.dtos.CreateOrderRequest;
import com.shashank.electronic.store.dtos.OrderDTO;
import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.exceptions.BadApiRequestException;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.helper.Helper;
import com.shashank.electronic.store.models.*;
import com.shashank.electronic.store.repositories.CartRepository;
import com.shashank.electronic.store.repositories.OrderRepository;
import com.shashank.electronic.store.repositories.UserRepository;
import com.shashank.electronic.store.services.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public OrderDTO createOrder(CreateOrderRequest orderDTO) {
        String userId = orderDTO.getUserId();
        String cartId = orderDTO.getCartId();
        //fetch user
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id !!"));
        //fetch cart
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart with given id not found on server !!"));
        List<CartItem> cartItems = cart.getItems();

        if (cartItems.size() <= 0) {
            throw new BadApiRequestException("Invalid number of items in cart !!");

        }

        //other checks
        Order order = Order.builder()
                .billingName(orderDTO.getBillingName())
                .billingPhone(orderDTO.getBillingPhone())
                .billingAddress(orderDTO.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(orderDTO.getPaymentStatus())
                .orderStatus(orderDTO.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

//        orderItems,amount
        AtomicReference<Double> orderAmount = new AtomicReference<Double>(0.0);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
//            CartItem->OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();

            orderAmount.set(orderAmount.get() + orderItem.getTotalPrice());
            return orderItem;
        }).collect(Collectors.toList());


        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        System.out.println(order.getOrderItems().size());

        //
        cart.getItems().clear();
        cartRepository.save(cart);
        Order savedOrder = orderRepository.save(order);
        return mapper.map(savedOrder, OrderDTO.class);


    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with id "));
        orderRepository.delete(order);

    }

    @Override
    public List<OrderDTO> getOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id "));
        List<Order> orders = orderRepository.findByUser(user);
        List<OrderDTO> orderDTOS = orders.stream().map(order -> mapper.map(order, OrderDTO.class)).collect(Collectors.toList());
        return orderDTOS;
    }

    @Override
    public PageableResponse<OrderDTO> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> page = orderRepository.findAll(pageable);
        PageableResponse<OrderDTO> response = Helper.getPageableResponse(page, OrderDTO.class);
        return response;
    }

    @Override
    public OrderDTO updateOrder(String orderId, OrderDTO orderDTO) {
        Order orderToUpdated = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with id "));
        orderToUpdated.setOrderStatus(orderDTO.getOrderStatus());
        orderToUpdated.setPaymentStatus(orderDTO.getPaymentStatus());
        Order updatedOrder = orderRepository.save(orderToUpdated);
        return mapper.map(updatedOrder, OrderDTO.class);
    }
}
