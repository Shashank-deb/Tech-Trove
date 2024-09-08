package com.shashank.electronic.store.models;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String orderId;
    /*PENDING,DELIVERED,DISPATCHED
     * using the enum for this
     * */
    private String orderStatus;

    /*NOT-PAID,PAID this will be boolean also and enum also
     * false->NOT-PAID
     * true->PAID
     * */
    private String paymentStatus;

    private double orderAmount;

    @Column(length = 10000)
    private String billingAddress;

    private String billingPhone;

    private String billingName;

    private Date orderedDate;

    private Date deliveredDate;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();


}
