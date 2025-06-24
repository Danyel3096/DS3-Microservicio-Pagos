package com.ds3.team8.payments_service.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    @NotNull(message = "El campo 'orderId' es obligatorio")
    private Long orderId;

    @NotBlank(message = "El campo 'customerId' es obligatorio")
    private Long customerId;

    @NotBlank(message = "El campo 'paymentMethod' es obligatorio")
    @Size(max = 50, message = "El campo 'paymentMethod' no puede exceder los 50 caracteres")
    private String paymentMethod;

    @NotNull(message = "El campo 'amount' es obligatorio")
    @Min(value = 0, message = "El campo 'amount' debe ser mayor o igual a 0")
    private BigDecimal amount;

    @NotBlank(message = "El campo 'currency' es obligatorio")
    @Size(max = 3, message = "El campo 'currency' debe tener exactamente 3 caracteres")
    private String currency;

    @NotBlank(message = "El campo 'chargeId' es obligatorio")
    private String chargeId;

    @NotBlank(message = "El campo 'paymentStatus' es obligatorio")
    @Size(max = 20, message = "El campo 'paymentStatus' no puede exceder los 20 caracteres")
    private String paymentStatus;
}
