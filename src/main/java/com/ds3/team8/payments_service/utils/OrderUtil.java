package com.ds3.team8.payments_service.utils;

import com.ds3.team8.payments_service.client.OrderClient;
import com.ds3.team8.payments_service.client.dtos.OrderItemResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OrderUtil {
    private static final Logger logger = LoggerFactory.getLogger(OrderUtil.class);

    private OrderUtil() {

    }

    public static List<OrderItemResponse> getOrderItems(OrderClient orderClient, Long orderId) {
        try {
            List<OrderItemResponse> items = orderClient.getOrderItemsByOrderId(orderId);
            logger.info("Se obtuvieron {} artículos para el pedido ID {}", items.size(), orderId);
            return items;
        } catch (FeignException e) {
            logger.error("Error al obtener los artículos del pedido {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("No se pudieron obtener los artículos del pedido", e);
        }
    }
}
