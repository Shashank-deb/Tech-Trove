package com.shashank.electronic.store.services.impl;


import com.shashank.electronic.store.dtos.AddItemToCartRequest;
import com.shashank.electronic.store.dtos.CartDTO;
import com.shashank.electronic.store.exceptions.BadApiRequestException;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.models.Cart;
import com.shashank.electronic.store.models.CartItem;
import com.shashank.electronic.store.models.Product;
import com.shashank.electronic.store.models.User;
import com.shashank.electronic.store.repositories.CartItemRepository;
import com.shashank.electronic.store.repositories.CartRepository;
import com.shashank.electronic.store.repositories.ProductRepository;
import com.shashank.electronic.store.repositories.UserRepository;
import com.shashank.electronic.store.services.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public CartDTO addItemToCart(String userId, AddItemToCartRequest request) {

        double quantity = request.getQuantity();
        String productId = request.getProductId();

        if (quantity <= 0) {
            throw new BadApiRequestException("Quantity should be greater than 0");
        }

        //fetch the product from db
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found exception"));
        //fetch the user from db 
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found exception"));

        //fetch the cart of a user in the db
        Cart cart = null;

        try {
            cart = cartRepository.findByUser(user).get();

        } catch (NoSuchElementException e) {
            cart = new Cart();
            cart.setCartId(UUID.randomUUID().toString());
            cart.setCreatedAt(new Date());
        }

        //perform cart operations
        //if cart items already present: then update the quantity
        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        List<CartItem> items = cart.getItems();
        items= items.stream().map(item -> {

            if (item.getProduct().getProductId().equals(productId)) {
                //item already present
                double newQuantity = item.getQuantity() + quantity;
                item.setQuantity(newQuantity);
                item.setTotalPrice(newQuantity * product.getDiscountedPrice());
                updated.set(true);
            }
            return item;
        }).collect(Collectors.toList());



//        cart.setItems(updatedItems);


        //create items

        if (!updated.get()) {
            //item is not present
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();
            cart.getItems().add(cartItem);

        }


        cart.setUser(user);
        Cart updatedCart = cartRepository.save(cart);
        return mapper.map(updatedCart, CartDTO.class);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItem) {
        CartItem cartItems = cartItemRepository.findById(cartItem).orElseThrow(() -> new ResourceNotFoundException("Cart item not found exception"));
        cartItemRepository.delete(cartItems);
    }

    @Override
    public void clearCart(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found exception"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found exception"));
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDTO getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found exception"));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart not found exception"));
        return mapper.map(cart, CartDTO.class);
    }
}
