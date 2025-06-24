package com.ds3.team8.payments_service.controllers;

import com.ds3.team8.payments_service.client.enums.Role;
import com.ds3.team8.payments_service.dtos.PaymentResponse;
import com.ds3.team8.payments_service.services.IPaymentService;
import com.ds3.team8.payments_service.utils.SecurityUtil;
import com.stripe.exception.SignatureVerificationException;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/v1/payments") // Indica la URL base para acceder a los servicios de esta clase
@Tag(name = "Pagos simulados", description = "Endpoints para pagos simulados")
public class PaymentController {
    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;
    private final IPaymentService paymentService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);


    public PaymentController(IPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Obtener todos los pagos
    @Operation(summary = "Obtener todos los pagos", description = "Obtener todos los pagos del sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
        @RequestHeader("X-Authenticated-User-Role") String roleHeader
    ) {
        SecurityUtil.validateRole(roleHeader, Role.ADMIN);
        return ResponseEntity.ok(paymentService.findAll());
    }

    // Procesar un pago de stripe
    @Hidden
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret); // tu secret desde @Value
        } catch (SignatureVerificationException e) {
            logger.error("Error verificando la firma del webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
        }

        logger.info("Evento recibido: {}", event.getType());

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = deserializer.getObject().orElse(null);

        if ("checkout.session.completed".equals(event.getType())) {
            if (stripeObject == null) {
                logger.warn("No se pudo deserializar checkout.session");
                return ResponseEntity.badRequest().body("No se pudo deserializar el objeto Session");
            }

            Session session = (Session) stripeObject;
            try {
                paymentService.processStripePayment(session);
                return ResponseEntity.ok("Pago procesado correctamente");
            } catch (Exception e) {
                logger.error("Error al procesar el pago", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error al procesar el pago: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Evento recibido pero no procesado");
    }

    // Buscar pagos con paginación
    // Ejemplo URL /api/v1/payments/pageable?page=0&size=8
    @Operation(summary = "Obtener los pagos con paginación", description = "Obtener los pagos con paginación del sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/pageable")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsPageable(
        Pageable pageable,
        @RequestHeader("X-Authenticated-User-Role") String roleHeader
    ) {
        SecurityUtil.validateRole(roleHeader, Role.ADMIN);
        return ResponseEntity.ok(paymentService.findAllPageable(pageable));
    }

    // Buscar un pago por ID
    @Operation(summary = "Buscar un pago por ID", description = "Buscar un pago por su ID en el sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        PaymentResponse paymentResponse = paymentService.findById(id);
        return ResponseEntity.ok(paymentResponse);
    }

    // Obtener los pagos de un cliente por su ID
    @Operation(summary = "Obtener los pagos de un cliente por ID", description = "Obtener los pagos de un cliente por su ID en el sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/user")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByCustomerId(
      @RequestHeader("X-Authenticated-User-Id") String userIdHeader
      ) {
        Long userId = SecurityUtil.parseUserId(userIdHeader);
        List<PaymentResponse> payments = paymentService.findByCustomerId(userId);
        return ResponseEntity.ok(payments);
    }

    // Obtener los pagos de un cliente por su ID con paginación
    @Operation(summary = "Obtener los pagos de un cliente por ID con paginación", description = "Obtener los pagos de un cliente por su ID con paginación en el sistema.", security = { @SecurityRequirement(name = "Bearer Authentication") })
    @GetMapping("/user/pageable")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByCustomerIdPageable(
      @RequestHeader("X-Authenticated-User-Id") String userIdHeader,
      Pageable pageable
    ) {
        Long userId = SecurityUtil.parseUserId(userIdHeader);
        Page<PaymentResponse> payments = paymentService.findByCustomerId(userId, pageable);
        return ResponseEntity.ok(payments);
    }
}
