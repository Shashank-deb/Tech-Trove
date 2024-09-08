package com.shashank.electronic.store.dtos;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AddItemToCartRequest {

    private String productId;
    private double quantity;
}
