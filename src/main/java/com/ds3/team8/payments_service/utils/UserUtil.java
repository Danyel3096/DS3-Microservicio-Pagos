package com.ds3.team8.payments_service.utils;

import com.ds3.team8.payments_service.client.UserClient;
import com.ds3.team8.payments_service.client.dtos.UserResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtil {
    private static final Logger logger = LoggerFactory.getLogger(UserUtil.class);

    private UserUtil() {

    }

    public static UserResponse validateUser(UserClient userClient, Long userId) {
        try {
            UserResponse user = userClient.getUserById(userId);
            logger.info("Usuario con ID {} validado correctamente", userId);
            return user;
        } catch (FeignException e) {
            logger.error("Error al validar el usuario: {}", e.getMessage(), e);
            throw new RuntimeException("No se pudo validar el usuario", e);
        }
    }
}
