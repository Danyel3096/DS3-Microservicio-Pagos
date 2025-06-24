package com.ds3.team8.payments_service.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ds3.team8.payments_service.client.enums.Role;
import com.ds3.team8.payments_service.exceptions.UnauthorizedException;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil() {

    }

    public static void validateRole(String roleHeader, Role... allowedRoles) {
        if (roleHeader == null || roleHeader.isBlank()) {
            logger.warn("Rol del usuario no especificado.");
            throw new UnauthorizedException("Rol del usuario no especificado.");
        }

        Role userRole = Role.valueOf(roleHeader);

        for (Role allowed : allowedRoles) {
            if (userRole == allowed) return;
        }

        logger.warn("El usuario con rol {} no tiene permisos suficientes. Roles permitidos: {}", userRole, allowedRoles);
        throw new UnauthorizedException("El usuario no tiene permisos suficientes.");
    }

    public static Long parseUserId(String userIdHeader) {
        if (userIdHeader == null || userIdHeader.isBlank()) {
            logger.warn("ID del usuario no especificado.");
            throw new UnauthorizedException("ID del usuario no especificado.");
        }

        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            logger.warn("ID de usuario inválido: {}", userIdHeader, e);
            throw new UnauthorizedException("ID de usuario inválido.");
        }
    }
}
