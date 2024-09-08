package com.shashank.electronic.store.dtos;

import com.shashank.electronic.store.models.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductDTO {


    private String productId;

    private String title;


    private String description;

    private double price;

    private double discountedPrice;

    private int quantity;

    private Date addedDate;

    private boolean live;

    private boolean stock;


    private String productImageName;

    private CategoryDTO category;
}
