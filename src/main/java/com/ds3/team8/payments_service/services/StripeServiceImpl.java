package com.ds3.team8.payments_service.services;

import com.ds3.team8.payments_service.client.OrderClient;
import com.ds3.team8.payments_service.client.ProductClient;
import com.ds3.team8.payments_service.client.UserClient;
import com.ds3.team8.payments_service.client.dtos.OrderItemResponse;
import com.ds3.team8.payments_service.client.dtos.OrderResponse;
import com.ds3.team8.payments_service.client.dtos.StockRequest;
import com.ds3.team8.payments_service.dtos.StripeRequest;
import com.ds3.team8.payments_service.dtos.StripeResponse;
import com.ds3.team8.payments_service.exceptions.BadRequestException;
import com.ds3.team8.payments_service.utils.OrderUtil;
import com.ds3.team8.payments_service.utils.UserUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import feign.FeignException;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements IStripeService {

    private final UserClient userClient;
    private final OrderClient orderClient;
    private final ProductClient productClient;

    private static final Logger logger = LoggerFactory.getLogger(StripeServiceImpl.class);

    @Value("${STRIPE_SUCCESS_URL}")
    private String successUrl;

    @Value("${STRIPE_CANCEL_URL}")
    private String cancelUrl;

    public StripeServiceImpl(@Value("${STRIPE_SECRET_KEY}") String stripeSecretKey, UserClient userClient, OrderClient orderClient, ProductClient productClient) {
        Stripe.apiKey = stripeSecretKey;
        this.userClient = userClient;
        this.orderClient = orderClient;
        this.productClient = productClient;
    }

    @Override
    public StripeResponse createCheckoutSession(StripeRequest stripeRequest, Long userId) {
        try {
            // Validar el usuario
            UserUtil.validateUser(userClient, userId);
            // Validar el pedido
            Long orderId = stripeRequest.getOrderId();
            OrderResponse orderResponse = validateOrder(orderId);

            // Obtener los items del pedido
            List<OrderItemResponse> orderItems = OrderUtil.getOrderItems(orderClient, orderId);
            // Validar la disponibilidad de los items
            validateStock(orderItems);

            BigDecimal orderAmount = orderResponse.getTotalAmount();
            Long amountInCents = orderAmount.multiply(BigDecimal.valueOf(100)).longValue();

            SessionCreateParams params = buildSessionParams(orderId, userId, amountInCents);

            Session session = Session.create(params);
            logger.info("Se ha creado una sesión de pago con ID: {}", session.getId());

            return new StripeResponse(session.getUrl());

        } catch (StripeException e) {
            logger.error("Error de Stripe al crear la sesión de pago: {}", e.getMessage(), e);
            throw new BadRequestException("Error con el proveedor de pagos");

        }
    }

    private SessionCreateParams buildSessionParams(Long orderId, Long userId, Long amountInCents) {
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("usd")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Pago de la orden #" + orderId)
                                                                .build())
                                                .build())
                                .build())
                .putMetadata("orderId", String.valueOf(orderId))
                .putMetadata("userId", String.valueOf(userId))
                .build();
    }

    private OrderResponse validateOrder(Long orderId) {
        try {
            OrderResponse orderResponse = orderClient.getOrderById(orderId);
            if (orderResponse == null) {
                logger.error("No se encontró la orden con ID {}", orderId);
                throw new BadRequestException("Orden no encontrada");
            }
            logger.info("Orden con ID {} validada correctamente", orderId);
            return orderResponse;
        } catch (FeignException e) {
            logger.error("Error al validar la orden: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo validar la orden", e);
        }
    }

    private void validateStock(List<OrderItemResponse> items) {
        List<StockRequest> stockRequests = items.stream()
                .map(i -> new StockRequest(i.getProductId(), -i.getQuantity()))
                .toList();
        try {
            if(productClient.validateStock(stockRequests)) {
                logger.info("Stock validado correctamente para los items del pedido");
            } else {
                logger.error("No hay suficiente stock para algunos items del pedido");
                throw new BadRequestException("No hay suficiente stock para algunos items del pedido");
            }
        } catch (FeignException e) {
            logger.error("Error al validar el stock de los items del pedido: {}", e.getMessage(), e);
            throw new RuntimeException("Error al validar el stock de los items del pedido");
        }
    }

}
