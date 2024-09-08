package com.shashank.electronic.store.dtos;


import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CartDTO {


    private String cartId;
    private Date createdAt;
    private UserDTO user;
    private List<CartItemDTO> items = new ArrayList<>();
}
