package com.vishal.electronicsstore.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.electronicsstore.dto.APIResponseMessage;
import com.vishal.electronicsstore.dto.CreateOrderRequest;
import com.vishal.electronicsstore.dto.OrderDTO;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.service.OrderService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersOfUser(@PathVariable String userId) {
        List<OrderDTO> ordersOfUser = orderService.getOrdersOfUser(userId);
        return ResponseEntity.ok(ordersOfUser);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<OrderDTO>> getOrders(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "orderDate", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        PageableResponse<OrderDTO> allOrders = orderService.getOrders(pageNumber, pageSize, sortBy, sortDirec);
        return ResponseEntity.ok(allOrders);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        OrderDTO createdOrder = orderService.createOrder(createOrderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<APIResponseMessage> removeOrder(@PathVariable String orderId) {
        orderService.removeOrder(orderId);
        APIResponseMessage response = APIResponseMessage.builder()
                .message("Order is removed.")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(response);
    }

}
