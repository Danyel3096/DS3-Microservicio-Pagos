package com.ds3.team8.payments_service.client.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StockRequest {
    @NotNull(message = "El campo 'productId' es obligatorio")
    private Long productId;

    @NotNull(message = "El campo 'quantity' es obligatorio")
    private Integer quantity;
}