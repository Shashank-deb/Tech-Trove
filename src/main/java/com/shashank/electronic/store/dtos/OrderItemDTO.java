package com.shashank.electronic.store.dtos;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderItemDTO {


    private int orderItemId;
    private double quantity;
    private double totalPrice;
    private ProductDTO product;

}
