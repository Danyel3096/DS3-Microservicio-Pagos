package com.ds3.team8.payments_service.mappers;

import com.ds3.team8.payments_service.dtos.PaymentRequest;
import com.ds3.team8.payments_service.dtos.PaymentResponse;
import com.ds3.team8.payments_service.entities.Payment;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaymentMapper {
    public PaymentResponse toPaymentResponse(Payment payment) {
        if (payment == null) return null;

        return new PaymentResponse(
                payment.getId(),
                payment.getCustomerId(),
                payment.getOrderId(),
                payment.getPaymentMethod(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getChargeId(),
                payment.getPaymentStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public Payment toPayment(PaymentRequest request) {
        if (request == null) return null;

        return new Payment(
                request.getOrderId(),
                request.getCustomerId(),
                request.getPaymentMethod(),
                request.getAmount(),
                request.getCurrency(),
                request.getChargeId(),
                request.getPaymentStatus()
        );
    }

    public Payment updatePayment(Payment payment, PaymentRequest request) {
        if (payment == null || request == null) return null;

        payment.setOrderId(request.getOrderId());
        payment.setCustomerId(request.getCustomerId());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrency());
        payment.setChargeId(request.getChargeId());
        payment.setPaymentStatus(request.getPaymentStatus());

        return payment;
    }

    public List<PaymentResponse> toPaymentResponseList(List<Payment> payments) {
        if (payments == null || payments.isEmpty()) return List.of();

        return payments.stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }
}
