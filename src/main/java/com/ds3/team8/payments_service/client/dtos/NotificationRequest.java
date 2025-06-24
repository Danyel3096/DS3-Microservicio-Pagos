package com.ds3.team8.payments_service.client.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    @NotBlank(message = "El campo 'message' es obligatorio")
    @Size(max = 500, message = "El campo 'message' no puede exceder los 500 caracteres")
    private String message;

    @NotNull(message = "El campo 'customerId' es obligatorio")
    private Long customerId;

    @NotNull(message = "El campo 'orderId' es obligatorio")
    private Long orderId;
}
