package com.shashank.electronic.store.dtos;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartItemDTO {


    private int cartItemId;
    private ProductDTO product;
    private int quantity;
    private int totalPrice;


}
