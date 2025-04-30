package com.vishal.electronicsstore.service.impl;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.CartDTO;
import com.vishal.electronicsstore.dto.CartItemDTO;
import com.vishal.electronicsstore.entity.Cart;
import com.vishal.electronicsstore.entity.CartItem;
import com.vishal.electronicsstore.entity.Product;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.exception.BadAPIRequestException;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.CartItemRepository;
import com.vishal.electronicsstore.repository.CartRepository;
import com.vishal.electronicsstore.repository.ProductRepository;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.service.CartService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CartServiceImpl(
            ProductRepository productRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartDTO addCartItemToCart(String userId, CartItemDTO cartItemDTO) {
        String productId = cartItemDTO.getProductId();
        int quantity = cartItemDTO.getQuantityOfCartItem();

        if (quantity <= 0) {
            throw new BadAPIRequestException("Requested quantity is not valid!");
        }

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ResourceNotFoundException("Product not found in database!"));

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found in database!"));

        Cart cart = null;
        try {
            cart = cartRepository.findByUser(user).get();
        } catch (NoSuchElementException e) {
            cart = Cart.builder()
                    .cartId(UUID.randomUUID().toString())
                    .createdAt(new Date())
                    .user(user)
                    .build();
        }

        // If cart items already present then update them
        AtomicReference<Boolean> isCartItemsUpdated = new AtomicReference<>(false);
        cart.getCartItems().stream().map(item -> {
            if (item.getProduct().getProductId().equals(productId)) {
                item.setQuantityOfCartItem(quantity);
                item.setPriceOfCartItem(quantity * product.getDiscountedPrice());
                isCartItemsUpdated.set(true);
            }
            return item;
        }).collect(Collectors.toList());

        if (!isCartItemsUpdated.get()) {
            CartItem cartItem = CartItem.builder()
                    .quantityOfCartItem(quantity)
                    .priceOfCartItem(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();

            cart.getCartItems().add(cartItem);
        }

        Cart updatedCart = cartRepository.save(cart);

        return modelMapper.map(updatedCart, CartDTO.class);
    }

    @Override
    public void removeCartItemFromCart(int cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new ResourceNotFoundException("Cart item not found in database!"));
        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found in database!"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Cart not found in database!"));

        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDTO getCartByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found in database!"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Cart not found in database!"));

        return modelMapper.map(cart, CartDTO.class);
    }

}
