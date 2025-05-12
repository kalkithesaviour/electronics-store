package com.vishal.electronicsstore.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vishal.electronicsstore.entity.CartItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {

    private String cartId;

    private Date createdAt;

    private UserDto user;

    private List<CartItem> cartItems = new ArrayList<>();

}
