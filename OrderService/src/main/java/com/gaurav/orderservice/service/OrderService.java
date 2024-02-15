package com.gaurav.orderservice.service;

import com.gaurav.orderservice.model.OrderRequest;
import com.gaurav.orderservice.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
