package com.vishal.electronicsstore.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vishal.electronicsstore.entity.OrderItem;
import com.vishal.electronicsstore.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String orderId;

    private String orderStatus;

    private String paymentStatus;

    private int orderAmount;

    private String billingAddress;

    private String billingPhone;

    private String billingName;

    private Date orderDate = new Date();

    private Date deliveryDate;

    private User user;

    private List<OrderItem> orderItems = new ArrayList<>();

}
