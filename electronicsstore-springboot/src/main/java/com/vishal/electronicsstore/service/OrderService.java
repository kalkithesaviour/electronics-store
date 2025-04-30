package com.vishal.electronicsstore.service;

import java.util.List;

import com.vishal.electronicsstore.dto.CreateOrderRequest;
import com.vishal.electronicsstore.dto.OrderDTO;
import com.vishal.electronicsstore.dto.PageableResponse;

public interface OrderService {

    OrderDTO createOrder(CreateOrderRequest createOrderRequest);

    void removeOrder(String orderId);

    List<OrderDTO> getOrdersOfUser(String userId);

    PageableResponse<OrderDTO> getOrders(int pageNumber, int pageSize, String sortBy, String sortDirec);

}
