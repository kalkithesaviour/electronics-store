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

import com.vishal.electronicsstore.dto.APIResponseMessage;
import com.vishal.electronicsstore.dto.CartDTO;
import com.vishal.electronicsstore.dto.CartItemDTO;
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

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable String userId) {
        CartDTO cartDTO = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CartDTO> addItemToCart(
            @RequestBody CartItemDTO cartItemDTO,
            @PathVariable String userId) {
        CartDTO updatedCartDTO = cartService.addCartItemToCart(userId, cartItemDTO);
        return ResponseEntity.ok(updatedCartDTO);
    }

    @DeleteMapping("/cart-item/{cartItemId}")
    public ResponseEntity<APIResponseMessage> removeItemFromCart(@PathVariable int cartItemId) {
        cartService.removeCartItemFromCart(cartItemId);
        APIResponseMessage response = APIResponseMessage.builder()
                .message("Cart item is removed.")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<APIResponseMessage> clearCartByUserId(@PathVariable String userId) {
        cartService.clearCart(userId);
        APIResponseMessage response = APIResponseMessage.builder()
                .message("Cart is now empty.")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

}
