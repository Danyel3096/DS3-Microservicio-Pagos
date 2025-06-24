package com.ds3.team8.payments_service.services;

import com.ds3.team8.payments_service.client.NotificationClient;
import com.ds3.team8.payments_service.client.OrderClient;
import com.ds3.team8.payments_service.client.ProductClient;
import com.ds3.team8.payments_service.client.UserClient;
import com.ds3.team8.payments_service.client.dtos.NotificationRequest;
import com.ds3.team8.payments_service.client.dtos.OrderItemResponse;
import com.ds3.team8.payments_service.client.dtos.OrderStatusRequest;
import com.ds3.team8.payments_service.client.dtos.StockRequest;
import com.ds3.team8.payments_service.client.enums.OrderStatus;
import com.ds3.team8.payments_service.dtos.PaymentRequest;
import com.ds3.team8.payments_service.dtos.PaymentResponse;
import com.ds3.team8.payments_service.entities.Payment;
import com.ds3.team8.payments_service.exceptions.NotFoundException;
import com.ds3.team8.payments_service.mappers.PaymentMapper;
import com.ds3.team8.payments_service.repositories.IPaymentRepository;

import com.ds3.team8.payments_service.utils.OrderUtil;
import com.ds3.team8.payments_service.utils.UserUtil;
import feign.FeignException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements IPaymentService {
    private final IPaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderClient orderClient;
    private final UserClient userClient;
    private final NotificationClient notificationClient;
    private final ProductClient productClient;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    public PaymentServiceImpl(IPaymentRepository paymentRepository, PaymentMapper paymentMapper, OrderClient orderClient, UserClient userClient, NotificationClient notificationClient, ProductClient productClient) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.orderClient = orderClient;
        this.userClient = userClient;
        this.notificationClient = notificationClient;
        this.productClient = productClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findAll() {
        List<Payment> payments = paymentRepository.findAllByIsActiveTrue();
        if (payments.isEmpty()) {
            logger.warn("No se encontraron pagos activos");
            throw new NotFoundException("No se encontraron pagos activos");
        }
        logger.info("Número de pagos encontrados: {}", payments.size());
        return paymentMapper.toPaymentResponseList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findAllPageable(Pageable pageable) {
        Page<Payment> payments = paymentRepository.findAllByIsActiveTrue(pageable);
        if (payments.isEmpty()) {
            logger.warn("No se encontraron pagos activos");
            throw new NotFoundException("No se encontraron pagos activos");
        }
        logger.info("Número de pagos encontrados: {}", payments.getTotalElements());
        return payments.map(paymentMapper::toPaymentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id) {
        Optional<Payment> payment = paymentRepository.findByIdAndIsActiveTrue(id);
        if (payment.isEmpty()) {
            logger.error("Pago con ID {} no encontrado", id);
            throw new NotFoundException("Pago no encontrado");
        }
        logger.info("Pago encontrado: {}", payment.get());
        return paymentMapper.toPaymentResponse(payment.get());
    }

    @Override
    @Transactional
    public void processStripePayment(Session session) {
        Long orderId = Long.parseLong(session.getMetadata().get("orderId"));
        Long userId = Long.parseLong(session.getMetadata().get("userId"));
        String chargeId = session.getPaymentIntent();

        // Validar que el pedido existe
        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(chargeId);
        } catch (StripeException e) {
            logger.error("Error al recuperar el PaymentIntent: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el pago", e);
        }

        String paymentMethod = paymentIntent.getPaymentMethodTypes().get(0);
        String currency = paymentIntent.getCurrency();
        Long amount = paymentIntent.getAmountReceived();
        String paymentStatus = paymentIntent.getStatus();

        logger.info("Procesando pago confirmado: orderId={}, userId={}, chargeId={}", orderId, userId, chargeId);

        // Validar que el pago no haya sido procesado previamente
        if (paymentRepository.existsByChargeIdAndIsActiveTrue(chargeId)) {
            logger.warn("El pago con chargeId {} ya fue procesado", chargeId);
            throw new RuntimeException("El pago ya fue procesado");
        }

        // Validar que el pago fue exitoso
        if (!"succeeded".equals(paymentStatus)) {
            logger.error("El pago no fue exitoso: status={}", paymentStatus);
            updateOrderStatus(orderId, OrderStatus.CANCELED);
            throw new RuntimeException("El pago no fue exitoso");
        }

        try {
            // Validar que el usuario existe
            UserUtil.validateUser(userClient, userId);
            // Obtener los artículos del pedido
            List<OrderItemResponse> orderItems = OrderUtil.getOrderItems(orderClient, orderId);
            // Validar que los artículos del pedido existen y actualizar el stock
            updateStock(orderItems);
            // Actualizar el estado del pedido a PAID
            updateOrderStatus(orderId, OrderStatus.PAID);
            // Enviar notificación al usuario
            sendNotification(userId, orderId);

            // Crear el pago
            Payment payment = paymentMapper.toPayment(new PaymentRequest(
                orderId,
                userId,
                paymentMethod,
                BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100)),
                currency,
                chargeId,
                paymentStatus
            ));

            // Guardar el pago en la base de datos
            paymentRepository.save(payment);
            logger.info("Pago procesado exitosamente para orderId {}", orderId);
        } catch (Exception e) {
            logger.error("Error al procesar el pago para orderId {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Error al procesar el pago", e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> findByCustomerId(Long customerId) {
        // Validar que el usuario existe
        UserUtil.validateUser(userClient, customerId);

        List<Payment> payments = paymentRepository.findAllByCustomerIdAndIsActiveTrue(customerId);
        if (payments.isEmpty()) {
            logger.warn("No se encontraron pagos para el cliente con ID {}", customerId);
            throw new NotFoundException("No se encontraron pagos para el cliente");
        }
        logger.info("Número de pagos encontrados para el cliente {}: {}", customerId, payments.size());
        return paymentMapper.toPaymentResponseList(payments);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponse> findByCustomerId(Long customerId, Pageable pageable) {
        // Validar que el usuario existe
        UserUtil.validateUser(userClient, customerId);

        Page<Payment> payments = paymentRepository.findAllByCustomerIdAndIsActiveTrue(customerId, pageable);
        if (payments.isEmpty()) {
            logger.warn("No se encontraron pagos para el cliente con ID {}", customerId);
            throw new NotFoundException("No se encontraron pagos para el cliente");
        }
        logger.info("Número de pagos encontrados para el cliente {}: {}", customerId, payments.getTotalElements());
        return payments.map(paymentMapper::toPaymentResponse);
    }

    private void updateStock(List<OrderItemResponse> items) {
        List<StockRequest> stockRequests = items.stream()
                .map(i -> new StockRequest(i.getProductId(), -i.getQuantity()))
                .toList();
        try {
            productClient.updateStock(stockRequests);
            logger.info("Stock actualizado para los productos: {}", stockRequests);
        } catch (FeignException e) {
            logger.error("Error al actualizar el stock: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo actualizar el stock", e);
        }
    }

    private void updateOrderStatus(Long orderId, OrderStatus orderStatus) {
        try {
            orderClient.updateOrderStatus(orderId, new OrderStatusRequest(orderStatus));
            logger.info("Estado del pedido {} actualizado a PAID", orderId);
        } catch (FeignException e) {
            logger.error("Error al actualizar el estado del pedido: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo actualizar el estado del pedido", e);
        }
    }

    private void sendNotification(Long userId, Long orderId) {
        try {
            notificationClient.saveNotification(new NotificationRequest(
                    "Su pago ha sido procesado exitosamente.",
                    userId,
                    orderId
            ));
            logger.info("Notificación enviada al usuario {} para el pedido {}", userId, orderId);
        } catch (FeignException e) {
            logger.error("Error al enviar la notificación: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo enviar la notificación", e);
        }
    }
}
