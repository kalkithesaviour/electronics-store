package com.vishal.electronicsstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.electronicsstore.dto.ApiResponseMessage;
import com.vishal.electronicsstore.dto.CartDto;
import com.vishal.electronicsstore.dto.CartItemDto;
import com.vishal.electronicsstore.service.CartService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/carts")
@Slf4j
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable String userId) {
        CartDto cartDto = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDto);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CartDto> addItemToCart(
            @RequestBody CartItemDto cartItemDto,
            @PathVariable String userId) {
        CartDto updatedCartDto = cartService.addCartItemToCart(userId, cartItemDto);
        return ResponseEntity.ok(updatedCartDto);
    }

    @DeleteMapping("/cart-item/{cartItemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable int cartItemId) {
        cartService.removeCartItemFromCart(cartItemId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Cart item is removed.")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCartByUserId(@PathVariable String userId) {
        cartService.clearCart(userId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Cart is now empty.")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

}
