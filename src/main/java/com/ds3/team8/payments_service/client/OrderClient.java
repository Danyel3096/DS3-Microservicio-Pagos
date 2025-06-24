package com.ds3.team8.payments_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.ds3.team8.payments_service.client.dtos.OrderItemResponse;
import com.ds3.team8.payments_service.client.dtos.OrderResponse;
import com.ds3.team8.payments_service.client.dtos.OrderStatusRequest;
import com.ds3.team8.payments_service.config.FeignClientInterceptor;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "orders-service", configuration = FeignClientInterceptor.class)
public interface OrderClient {
    
    @GetMapping("/api/v1/orders/{id}")
    OrderResponse getOrderById(@PathVariable("id") Long id);

    @PostMapping("/api/v1/orders/{id}/status")
    OrderResponse updateOrderStatus(@PathVariable("id") Long id, @RequestBody OrderStatusRequest orderStatusRequest);

    @GetMapping("/api/v1/order-items/order/{orderId}")
    List<OrderItemResponse> getOrderItemsByOrderId(@PathVariable("orderId") Long orderId);
}
