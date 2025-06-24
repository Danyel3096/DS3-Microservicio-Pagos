package com.ds3.team8.payments_service.controllers;

import com.ds3.team8.payments_service.dtos.StripeRequest;
import com.ds3.team8.payments_service.dtos.StripeResponse;
import com.ds3.team8.payments_service.services.IStripeService;
import com.ds3.team8.payments_service.utils.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/v1/stripe") // Indica la URL base para acceder a los servicios de esta clase
@Tag(name = "Stripe pagos simulados", description = "Endpoints para stripe pagos simulados")
public class StripeController {
    private final IStripeService stripeService;

    public StripeController(IStripeService stripeService) {
        this.stripeService = stripeService;
    }

    // Crear un pago simulado con stripe
    @Operation(summary = "Crear un pago simulado con Stripe", description = "Crea una sesión de pago con Stripe.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @PostMapping
    public ResponseEntity<StripeResponse> createCheckoutSession(
        @Valid @RequestBody StripeRequest stripeRequest,
        @RequestHeader("X-Authenticated-User-Id") String userIdHeader
    ) {
        Long userId = SecurityUtil.parseUserId(userIdHeader);
        StripeResponse savedStripe = stripeService.createCheckoutSession(stripeRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStripe);
    }

    // Mostrar mensaje de éxito
    @Operation(summary = "Mostrar mensaje de éxito", description = "Muestra un mensaje de éxito después de un pago simulado con Stripe.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/success")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("Pago simulado exitoso con Stripe");
    }

    // Mostrar mensaje de cancelación
    @Operation(summary = "Mostrar mensaje de cancelación", description = "Muestra un mensaje de cancelación después de un pago simulado con Stripe.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/cancel")
    public ResponseEntity<String> cancel() {
        return ResponseEntity.ok("Pago simulado cancelado con Stripe");
    }
}
