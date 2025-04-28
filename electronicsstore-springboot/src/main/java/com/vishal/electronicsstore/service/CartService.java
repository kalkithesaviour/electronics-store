package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.CartDTO;
import com.vishal.electronicsstore.dto.CartItemDTO;

public interface CartService {

    // Add items to the cart has two cases:
    // 1: create a cart for the user if one not available, and then add the items
    // 2: cart already available to add the items
    CartDTO addCartItemToCart(String userId, CartItemDTO cartItemDTO);

    // Remove item from the cart
    void removeCartItemFromCart(int cartItemId);

    // Remove all items from the cart
    void clearCart(String userId);

    CartDTO getCartByUserId(String userId);

}
