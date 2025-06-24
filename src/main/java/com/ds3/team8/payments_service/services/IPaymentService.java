package com.ds3.team8.payments_service.services;

import com.ds3.team8.payments_service.dtos.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.stripe.model.checkout.Session;

import java.util.List;

public interface IPaymentService {
    List<PaymentResponse> findAll(); // Obtener todos los pagos
    void processStripePayment(Session session); // Procesar un pago de Stripe
    Page<PaymentResponse> findAllPageable(Pageable pageable); // Obtener todos los pagos con paginación
    PaymentResponse findById(Long id); // Obtener un pago por su ID
    List<PaymentResponse> findByCustomerId(Long customerId); // Obtener pagos por ID de cliente
    Page<PaymentResponse> findByCustomerId(Long customerId, Pageable pageable); // Obtener pagos por ID de cliente con paginación
}
