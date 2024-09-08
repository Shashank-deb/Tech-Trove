package com.shashank.electronic.store.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name="category_id")
    private String categoryId;
    @Column(name="category_title",length =100,nullable = false)
    private String title;
    @Column(name="category_description")
    private String description;
    @Column(name="cover_image")
    private String coverImage;

    //One category has many products
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Product> products=new ArrayList<>();

}
