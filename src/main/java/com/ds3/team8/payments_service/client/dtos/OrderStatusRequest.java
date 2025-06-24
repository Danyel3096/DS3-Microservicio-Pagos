package com.ds3.team8.payments_service.client.dtos;

import com.ds3.team8.payments_service.client.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusRequest {
    @NotNull(message = "El campo 'orderStatus' es obligatorio")
    private OrderStatus orderStatus;
}