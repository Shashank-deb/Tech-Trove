package com.shashank.electronic.store.controllers;


import com.shashank.electronic.store.config.AppConstants;
import com.shashank.electronic.store.dtos.APIResponseMessage;
import com.shashank.electronic.store.dtos.AddItemToCartRequest;
import com.shashank.electronic.store.dtos.CartDTO;
import com.shashank.electronic.store.services.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "CartController", description = "REST APIs related to cart operations")

public class CartController {
    @Autowired
    private CartService cartService;


    @PreAuthorize("hasAnyRole('"+ AppConstants.ROLE_ADMIN +"','"+AppConstants.ROLE_NORMAL+"')")
    @PostMapping("/{userId}")
    public ResponseEntity<CartDTO> addItemsToCart(
            @PathVariable("userId") String userId,
            @RequestBody AddItemToCartRequest request
    ) {
        CartDTO cartDTO = cartService.addItemToCart(userId, request);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('"+ AppConstants.ROLE_ADMIN +"','"+AppConstants.ROLE_NORMAL+"')")
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<APIResponseMessage> removeItemFromCart(
            @PathVariable("userId") String userId,
            @PathVariable("itemId") int itemId
    ) {
        cartService.removeItemFromCart(userId, itemId);
        APIResponseMessage response = APIResponseMessage.builder().message("Item removed from cart").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('"+ AppConstants.ROLE_ADMIN +"','"+AppConstants.ROLE_NORMAL+"')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<APIResponseMessage> clearCart(
            @PathVariable("userId") String userId
    ) {
        cartService.clearCart(userId);
        APIResponseMessage response = APIResponseMessage.builder().message("Cart cleared").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('"+ AppConstants.ROLE_ADMIN +"','"+AppConstants.ROLE_NORMAL+"')")
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(
            @PathVariable("userId") String userId
    ) {
        CartDTO cartDTO = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }


}
