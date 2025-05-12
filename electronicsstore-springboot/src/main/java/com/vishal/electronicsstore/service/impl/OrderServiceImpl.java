package com.vishal.electronicsstore.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.CreateOrderRequest;
import com.vishal.electronicsstore.dto.OrderDto;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.entity.Cart;
import com.vishal.electronicsstore.entity.CartItem;
import com.vishal.electronicsstore.entity.Order;
import com.vishal.electronicsstore.entity.OrderItem;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.exception.BadAPIRequestException;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.CartRepository;
import com.vishal.electronicsstore.repository.OrderRepository;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.service.OrderService;
import com.vishal.electronicsstore.util.PageableUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            UserRepository userRepository,
            CartRepository cartRepository,
            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderDto createOrder(CreateOrderRequest createOrderRequest) {
        User user = userRepository.findById(createOrderRequest.getUserId()).orElseThrow(
                () -> new ResourceNotFoundException("User not found in database!"));

        Cart cart = cartRepository.findByUser(user).orElseThrow(
                () -> new ResourceNotFoundException("Cart not found in database!"));

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.size() <= 0) {
            throw new BadAPIRequestException("Invalid number of items in the cart!");
        }

        Order order = Order.builder()
                .billingName(createOrderRequest.getBillingName())
                .billingPhone(createOrderRequest.getBillingPhone())
                .billingAddress(createOrderRequest.getBillingAddress())
                .orderDate(new Date())
                .deliveryDate(null)
                .paymentStatus(createOrderRequest.getPaymentStatus())
                .orderStatus(createOrderRequest.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantityOfCartItem())
                    .product(cartItem.getProduct())
                    .priceOfOrderItem(cartItem.getPriceOfCartItem())
                    .order(order)
                    .build();
            orderAmount.set(orderAmount.get() + orderItem.getPriceOfOrderItem());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        cart.getCartItems().clear();
        cartRepository.save(cart);
        Order savedOrder = orderRepository.save(order);

        return modelMapper.map(savedOrder, OrderDto.class);
    }

    @Override
    public void removeOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new ResourceNotFoundException("Order not found in database!"));
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getOrdersOfUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found in database!"));

        List<Order> orders = orderRepository.findByUser(user);
        List<OrderDto> orderDtos = orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
        return orderDtos;
    }

    @Override
    public PageableResponse<OrderDto> getOrders(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Sort sort = sortDirec.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(ordersPage, OrderDto.class, modelMapper);
    }

}
