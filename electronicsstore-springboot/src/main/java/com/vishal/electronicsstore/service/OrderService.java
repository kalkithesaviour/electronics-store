package com.vishal.electronicsstore.service;

import java.util.List;

import com.vishal.electronicsstore.dto.CreateOrderRequest;
import com.vishal.electronicsstore.dto.OrderDto;
import com.vishal.electronicsstore.dto.PageableResponse;

public interface OrderService {

    OrderDto createOrder(CreateOrderRequest createOrderRequest);

    void removeOrder(String orderId);

    List<OrderDto> getOrdersOfUser(String userId);

    PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDirec);

}
