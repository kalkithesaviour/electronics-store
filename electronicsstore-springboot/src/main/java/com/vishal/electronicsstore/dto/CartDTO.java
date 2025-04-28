package com.vishal.electronicsstore.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vishal.electronicsstore.entity.CartItem;
import com.vishal.electronicsstore.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private String cartId;

    private Date createdAt;

    private User user;

    private List<CartItem> cartItems = new ArrayList<>();

}
