package com.shashank.electronic.store.services;

import com.shashank.electronic.store.dtos.AddItemToCartRequest;
import com.shashank.electronic.store.dtos.CartDTO;
import com.shashank.electronic.store.dtos.ProductDTO;

public interface CartService {


    //add items to cart
    //case 1: cart for the user is not available : we will create the cart and then add the items
    //case 2: cart is available : add items to the cart directly
    CartDTO addItemToCart(String userId, AddItemToCartRequest request);

    //remove item from cart
    void removeItemFromCart(String userId, int cartItem);

    //remove all items from cart of the user
    void clearCart(String userId);


    CartDTO getCartByUser(String userId);


}
