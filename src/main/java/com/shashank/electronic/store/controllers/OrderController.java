package com.shashank.electronic.store.controllers;


import com.shashank.electronic.store.dtos.APIResponseMessage;
import com.shashank.electronic.store.dtos.CreateOrderRequest;
import com.shashank.electronic.store.dtos.OrderDTO;
import com.shashank.electronic.store.dtos.PageableResponse;

import com.shashank.electronic.store.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@Tag(name = "OrderController", description = "REST APIs related to perform order operations")

public class OrderController {


    @Autowired
    private OrderService orderService;


    //user
    //create

    @PreAuthorize("hasAnyRole('NORMAL','ADMIN')")
    @PostMapping
    //create
    public ResponseEntity<OrderDTO> createOrder(
            @Valid
            @RequestBody CreateOrderRequest request
    ) {
        OrderDTO order = orderService.createOrder(request);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<APIResponseMessage> removeOrder(
            @PathVariable("orderId") String orderId
    ) {
        orderService.removeOrder(orderId);
        APIResponseMessage response = APIResponseMessage.builder().message("Order removed").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('NORMAL','ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersOfUser(
            @PathVariable("userId") String userId
    ) {
        List<OrderDTO> ordersOfUser = orderService.getOrdersOfUser(userId);
        return new ResponseEntity<>(ordersOfUser, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PageableResponse<OrderDTO>> getOrders(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "orderedDate", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc", required = false) String sortDir
    ) {

        PageableResponse<OrderDTO> orders = orderService.getOrders(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(orders, HttpStatus.OK);

    }


    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(
            @RequestBody OrderDTO orderDTO,
            @PathVariable("orderId") String orderId
    ) {
        OrderDTO updateOrder = orderService.updateOrder(orderId, orderDTO);
        return new ResponseEntity<>(updateOrder, HttpStatus.OK);
    }


}
