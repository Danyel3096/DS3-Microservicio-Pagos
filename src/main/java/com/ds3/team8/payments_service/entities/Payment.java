package com.ds3.team8.payments_service.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Genera automáticamente getters, setters, equals, hashCode y toString
@NoArgsConstructor  // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
@Entity  // Indica que esta clase es una entidad JPA
@Table(name = "payments")  // Nombre de la tabla en la base de datos
public class Payment {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincremental
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId; // ID del cliente

    @Column(name = "order_id", nullable = false)
    private Long orderId; // ID del pedido

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // Método de pago

    @Column(nullable = false)
    private BigDecimal amount; // Monto del pago

    @Column(name = "currency", nullable = false)
    private String currency; // Moneda del pago

    @Column(name = "charge_id", nullable = false)
    private String chargeId; // ID del cargo

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // Estado del pago

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Payment(Long orderId, Long customerId, String paymentMethod, BigDecimal amount, String currency, String chargeId, String paymentStatus) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.currency = currency;
        this.chargeId = chargeId;
        this.paymentStatus = paymentStatus;
    }
}

