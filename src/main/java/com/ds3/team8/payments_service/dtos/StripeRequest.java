package com.ds3.team8.payments_service.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StripeRequest {
    @NotNull(message = "El campo 'orderId' es obligatorio")
    private Long orderId;
}
